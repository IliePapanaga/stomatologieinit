package com.cl.mdd.server.core.service.user;

import com.cl.mdd.server.core.data.model.UserValidationModel;

import javax.validation.Valid;

public interface UserValidationService {

    void validateEmail(@Valid UserValidationModel model);
}
