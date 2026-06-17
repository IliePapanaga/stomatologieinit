package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;

abstract class UserModelFactory<T extends UserModel> extends AbstractModelFactory<T> {

    @Autowired
    private ContactModelFactory contactFactory;

    @Override
    public T fillFields(T userModel) {
        userModel.setContact(contactFactory.create());
        return userModel;
    }
}
