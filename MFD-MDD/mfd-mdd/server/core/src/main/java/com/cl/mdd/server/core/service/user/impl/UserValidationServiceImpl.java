package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.UserValidationModel;
import com.cl.mdd.server.core.service.user.UserValidationService;
import com.cl.mdd.server.core.validation.group.Email;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class UserValidationServiceImpl implements UserValidationService {

    @Override
    @Validated(value = {Email.class})
    public void validateEmail(@Valid UserValidationModel model) {
    }

}
