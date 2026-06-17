package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.group.Register;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class RegisterPracticeOwner extends RegisterUser {

    @Valid
    @NotNull(groups = {Register.class})
    private RegisterPractice registerPractice;

    public RegisterPractice getRegisterPractice() {
        return registerPractice;
    }

    public RegisterPracticeOwner setRegisterPractice(RegisterPractice registerPractice) {
        this.registerPractice = registerPractice;
        return this;
    }
}
