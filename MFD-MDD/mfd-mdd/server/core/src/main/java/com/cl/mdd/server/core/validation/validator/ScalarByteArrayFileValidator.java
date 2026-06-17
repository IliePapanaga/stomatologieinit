package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.validation.annotation.Validator;

@Validator
public class ScalarByteArrayFileValidator extends AbstractFileValidator<byte[]> {

    @Override
    protected byte[] content(byte[] file) {
        return file;
    }
}
