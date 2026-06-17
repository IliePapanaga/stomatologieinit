package com.cl.mdd.server.core.service.contact;

import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.ContactPhotoModel;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;

public interface ContactService extends Service {

    void updateUserPhoto(String userId, @Valid ContactPhotoModel photo);

    void updateUserContact(String userId, @Valid ContactModel contact);

    ContactPhotoModel getUserPhoto(String userId);
}
