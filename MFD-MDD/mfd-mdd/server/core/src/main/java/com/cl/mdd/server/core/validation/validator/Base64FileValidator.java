package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.validation.annotation.Validator;

import java.util.Base64;

@Validator
public class Base64FileValidator extends AbstractFileValidator<String> {

    @Override
    protected byte[] content(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

}
