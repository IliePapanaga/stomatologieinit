package com.cl.mdd.server.core.service.payment.impl.primerate;

import com.cl.mdd.server.core.service.payment.impl.primerate.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * PrimeRate payment gateway client.
 * <p />
 * Uses the <span>Three Step Redirect API</span> and <span>Query API</span> from
 * <a href="https://primeratemerchant.transactiongateway.com/merchants/resources/integration/integration_portal.php">here</a>.
 */
@Component
public class GatewayClient {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${payment.primerate.url.3step:https://primeratemerchant.transactiongateway.com/api/v2/three-step}")
    private String threeStepUrl;

    @Value("${payment.primerate.url.query:https://primeratemerchant.transactiongateway.com/api/query.php}")
    private String queryUrl;

    private final RestTemplate primerateRestTemplate;

    @Autowired
    public GatewayClient(RestTemplate primerateRestTemplate) {
        this.primerateRestTemplate = primerateRestTemplate;
    }

    <T, R> T post(String url, R request, MediaType mediaType, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        ResponseEntity<T> responseEntity =
                primerateRestTemplate.postForEntity(url, new HttpEntity<>(request, headers), responseType);
        if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        }
        else {
            throw new IllegalStateException(
                    "PrimeRate does not answer properly. " + url + " " + responseEntity.getStatusCode());
        }
    }

    <T, R> T post(R request, Class<T> responseType) {
        return post(threeStepUrl, request, MediaType.TEXT_XML, responseType);
    }

    String apiKey(Credentials credentials) {
        String key = credentials.getApiKey();
        if(StringUtils.isBlank(key)) {
            throw new IllegalStateException("PrimeRate key to access API is missing.");
        }
        return key;
    }

    FormUrlResponse urlResponseForPaymentMethod(Credentials credentials, String callbackUrl) {
        AddCustomer request = new AddCustomer();
        request.setApiKey(apiKey(credentials));
        request.setRedirectUrl(callbackUrl);
        return post(request, FormUrlResponse.class);
    }

    public String urlForPaymentMethod(Credentials credentials, String callbackUrl) {
        return urlResponseForPaymentMethod(credentials, callbackUrl).getFormUrl();
    }

    public VaultResponse completeVault(Credentials credentials, String token) {
        CompletionRequest request = new CompletionRequest();
        request.setApiKey(apiKey(credentials));
        request.setTokenId(token);
        return post(request, VaultResponse.class);
    }

    TxResponse transaction(Credentials credentials, String orderId) {
        // TODO: encode password, e.g. org.springframework.web.util.UriUtils
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>(3);
        request.add("order_id", orderId);
        request.add("username", credentials.getLogin());
        request.add("password", credentials.getPassword());
//        String request = "order_id=" + orderId
//                + "&username=" + credentials.getLogin() + "&password=" + credentials.getPassword();
        logger.debug("checking status for order [{}] as [{}]", orderId, credentials.getLogin());
        return post(queryUrl, request, MediaType.APPLICATION_FORM_URLENCODED, TxResponse.class);
    }

    public Result check(Credentials credentials, String orderId) {
        TxResponse tx = transaction(credentials, orderId);
        if(tx.getTransaction() == null) {
            logger.debug("status for order [{}] not found", orderId);
            return null;
        }
        else {
            TxResponse.Action lastAction = tx.getTransaction().getActions()
                    .get(tx.getTransaction().getActions().size() - 1);
            Result result;
            if(TxResponse.STATUS_COMPLETE.equalsIgnoreCase(tx.getTransaction().getCondition())
                    || TxResponse.STATUS_PENDING.equalsIgnoreCase(tx.getTransaction().getCondition())
                            && TxResponse.TransactionType.CC.equals(tx.getTransaction().getType())) {
                // pendingsettlement is OK for CC
                result = Result.successful(lastAction.getResponseText(), tx.getTransaction().getId());
            }
            else if(TxResponse.STATUS_PENDING.equalsIgnoreCase(tx.getTransaction().getCondition())
                    && TxResponse.TransactionType.ACH.equals(tx.getTransaction().getType())) {
                // pendingsettlement for ACH
                result = Result.pending(lastAction.getResponseText(), tx.getTransaction().getId());
            }
            else {
                result = Result.failed(lastAction.getResponseText(), tx.getTransaction().getId());
            }
            logger.debug("status for order [{}] is: {} / {}, tx {}",
                    orderId, result.getStatus(), result.getMessage(), result.getTransactionId());
            return result;
        }
    }

    public Result pay(Credentials credentials, String vaultId,
                      String orderId, BigDecimal amount, String comment) {
        Sale saleRequest = new Sale();
        saleRequest.setApiKey(apiKey(credentials));
        saleRequest.setVaultId(vaultId);
        saleRequest.setOrderId(orderId);
        saleRequest.setAmount(amount.toString());
        saleRequest.setOrderDescription(comment);
        logger.debug("paying order [{}] of {} ({})", orderId, amount, comment);
        SaleResponse response = post(saleRequest, SaleResponse.class);
        logger.debug("order [{}] result is: {} / {}", orderId, response.getResult(), response.getResultText());
        if(EndResponse.RESULT_OK.equals(response.getResult())) {
            return response.getBilling() != null && !response.getBilling().creditCard()
                    ? Result.pending(response.getResultText(), response.getTransactionId())
                    : Result.successful(response.getResultText(), response.getTransactionId());
        }
        else {
            return Result.failed(response.getResultText(), response.getTransactionId());
        }
    }

    public String urlForPayment(
            Credentials credentials, String orderId, BigDecimal amount, String comment, String callbackUrl) {
        Sale saleRequest = new Sale();
        saleRequest.setApiKey(apiKey(credentials));
        saleRequest.setRedirectUrl(callbackUrl);
        saleRequest.setOrderId(orderId);
        saleRequest.setOrderDescription(comment);
        saleRequest.setAmount(amount.toString());
        return post(saleRequest, FormUrlResponse.class).getFormUrl();
    }

    public Result completeTransaction(Credentials credentials, String token) {
        CompletionRequest request = new CompletionRequest();
        request.setApiKey(apiKey(credentials));
        request.setTokenId(token);
        SaleResponse response = post(request, SaleResponse.class);
        return EndResponse.RESULT_OK.equals(response.getResult())
                ? Result.successful(response.getResultText(), response.getTransactionId())
                : Result.failed(response.getResultText(), response.getTransactionId());
    }

    public void deleteVault(Credentials credentials, String vaultId) {
        post(new DeleteCustomer(apiKey(credentials), vaultId), VaultResponse.class);
    }

    /**
     * Validates the login/password.
     * <p />
     * Tries to get non existent transaction details. Differs the not found and unauthenticated answers.
     * @param credentials credentials
     * @return <code>true</code> if login and password okay
     */
    public boolean validateLoginPass(Credentials credentials) {
        TxResponse response = transaction(credentials, "1000000000");
        return StringUtils.isBlank(response.getErrorResponse());
    }

    /**
     * Validates the API key.
     * <p />
     * Tries to get submit URL for customer vault. Ignores successful result and understands the answer to a wrong key.
     * @param credentials credentials
     * @param urlBase server URL base to avoid using something too artificial
     * @return <code>true</code> if API key okay
     */
    public boolean validateApiKey(Credentials credentials, String urlBase) {
        FormUrlResponse response = urlResponseForPaymentMethod(credentials, urlBase);
        return response.getResult() == 1;
    }
}
