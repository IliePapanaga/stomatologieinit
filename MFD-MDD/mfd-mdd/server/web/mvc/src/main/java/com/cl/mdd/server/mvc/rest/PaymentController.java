package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.manager.payment.PaymentManager;
import com.cl.mdd.server.core.service.payment.PaymentInstrumentProblem;
import com.cl.mdd.server.core.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;

@Controller
@RequestMapping("/api/v1/payment")
public class PaymentController {

    @Value("${payment.ui.page.vault.success:/WEB-INF/jsp/vault_success.jsp}")
    private String pageVaultSuccess;

    @Value("${payment.ui.page.vault.error:/WEB-INF/jsp/vault_error.jsp}")
    private String pageVaultError;

    @Value("${payment.ui.page.tx.success:/WEB-INF/jsp/payment_success.jsp}")
    private String pageTxSuccess;

    @Value("${payment.ui.page.tx.error:/WEB-INF/jsp/payment_error.jsp}")
    private String pageTxError;

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vault/{localId}")
    public View completeVault(
            @PathVariable("localId") String localId, @RequestParam("token-id") String token, ModelMap model) {
        String view = pageVaultSuccess;
        try {
            model.addAttribute("paymentMethodId", paymentService.completeVault(localId, token));
        }
        catch (PaymentInstrumentProblem e) {
            view = pageVaultError;
            model.addAttribute("errorMessage", e.getMessage());
        }
        return new InternalResourceView(view);
    }

    @GetMapping("/pay/{localId}")
    public View completePayment(
            @PathVariable("localId") String localId, @RequestParam("token-id") String token/*, ModelMap model*/) {
        return new InternalResourceView(paymentService.completeTransaction(localId, token)
                ? pageTxSuccess : pageTxError);
    }

    @Autowired PaymentManager paymentManager;
    @RequestMapping("/test")
    public void test() {
        paymentManager.payTemporary("69e41358-ba79-4db8-bdc3-0c5c989bc9b0");
    }

}
