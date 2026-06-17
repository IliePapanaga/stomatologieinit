package com.cl.mdd.server.mvc.security.impersonation;

import com.cl.mdd.server.core.data.model.UserInfo;
import com.cl.mdd.server.core.data.model.errors.ErrorInfoModel;
import com.cl.mdd.server.mvc.rest.graphql.provider.AccountProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Collections;

@Controller
@RequestMapping("/impersonate")
public class ImpersonateController {

    private AccountProvider accountProvider;

    @Autowired
    public ImpersonateController(AccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }

    @PostMapping(value = "/failed", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<Collection<ErrorInfoModel>> failed(
            @RequestAttribute(value = WebAttributes.AUTHENTICATION_EXCEPTION) AuthenticationException exception) {

        ErrorInfoModel errorInfoModel = new ErrorInfoModel();
        errorInfoModel.setMessage(exception.getLocalizedMessage());
        errorInfoModel.setErrorCode(HttpStatus.BAD_REQUEST.toString());
        errorInfoModel.setExceptionClass(exception.getClass().getSimpleName());

        return new ResponseEntity<>(Collections.singletonList(errorInfoModel), HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/success", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<UserInfo> success() {
        return new ResponseEntity<>(accountProvider.currentAuthenticatedUserInfo(), HttpStatus.OK);
    }
}
