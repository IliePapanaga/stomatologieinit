package com.cl.mdd.server.core.validation.validator.payment;

import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;
import com.cl.mdd.server.core.service.payment.impl.primerate.Credentials;
import com.cl.mdd.server.core.service.payment.impl.primerate.GatewayClient;
import com.cl.mdd.server.core.settings.SystemSettings;
import com.cl.mdd.server.core.validation.constraint.payment.GatewayCredentials;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static com.cl.mdd.server.core.settings.Settings.PaymentPrimeRateSettings.*;

public class GatewayCredentialsValidator implements ConstraintValidator<GatewayCredentials, List<SystemSettingModel>> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SystemSettings systemSettings;

    @Autowired
    private GatewayClient gateway;

    @Value("${payment.validate.key.url:${mdd.domain}/api/v1/payment/validate/}")
    private String apiKeyUrl;

    @Override
    public void initialize(GatewayCredentials constraintAnnotation) {
    }

    String passwordValue(String value) {
        if(SystemSettingModel.ENCRYPTED_VALUE_MASK.equals(value)) {
            String dbPass = systemSettings.getString(PASSWORD.getKey());
            if(dbPass != null) {
                value = dbPass;
            }
            else {
                logger.warn("password value is encrypted, but there is no existing value in the DB");
            }
        }
        return value;
    }

    @Override
    public boolean isValid(List<SystemSettingModel> list, ConstraintValidatorContext context) {
        String login = null, pass = null, key = null;
        for(SystemSettingModel setting : CollectionUtils.emptyIfNull(list)) {
            if(API_KEY.getKey().equals(setting.getKey())) {
                key = setting.getValue();
            }
            else if(LOGIN.getKey().equals(setting.getKey())) {
                login = setting.getValue();
            }
            else if(PASSWORD.getKey().equals(setting.getKey())) {
                pass = passwordValue(setting.getValue());
            }
        }
        Credentials credentials = new Credentials(login, pass, key);
        if((login != null || pass != null) && !gateway.validateLoginPass(credentials)) {
            logger.debug("Prime Rate login/pass invalid ({}/***)", login);
            return false;
        }
        if(key != null && !gateway.validateApiKey(credentials, apiKeyUrl)) {
            logger.debug("Prime Rate API key invalid");
            return false;
        }
        return true;
    }
}
