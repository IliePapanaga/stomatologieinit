package com.cl.mdd.server.core.service.contact;

import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.ContactPhotoModel;
import com.cl.mdd.server.core.data.persistent.access.contact.ContactDao;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.contact.ContactPhoto;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.validation.group.Save;
import com.cl.mdd.server.core.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Validated
public class ContactServiceImpl extends ServiceSupport implements ContactService {

    @Autowired
    private ContactDao contactDao;

    @Override
    @Transactional
    @Validated(Save.class)
    public void updateUserPhoto(String userId, ContactPhotoModel photo) {
        Contact userContact = contactDao.findUserContact(userId);

        ContactPhoto contactPhoto = userContact.getContactPhoto();
        if (isNull(contactPhoto)) {
            contactPhoto = new ContactPhoto();
            userContact.setContactPhoto(contactPhoto);
        }
        contactPhoto.setContent(photo.getContent());
        contactPhoto.setContentType(photo.getContentType());
        contactPhoto.setName(photo.getName());
        contactDao.saveAndFlush(userContact);
    }

    @Override
    @Transactional
    @Validated(Update.class)
    public void updateUserContact(String userId, ContactModel contact) {
        Contact userContact = contactDao.findUserContact(userId);
        if (nonNull(userContact)) {
            Contact toBeMerged = commonConverter.toContact(contact);

            toBeMerged.setEmail(userContact.getEmail()); // for user we do not update email.
            toBeMerged.setContactPhoto(userContact.getContactPhoto()); // photo is handled separately.
            toBeMerged.setId(userContact.getId());
            toBeMerged.getAddress().setId(userContact.getAddress().getId());
            contactDao.save(toBeMerged);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ContactPhotoModel getUserPhoto(String userId) {
        Contact userContact = contactDao.findUserContact(userId);

        ContactPhoto contactPhoto = userContact.getContactPhoto();
        if (isNull(contactPhoto)) {
            return null;
        } else {
            ContactPhotoModel photo = new ContactPhotoModel();
            photo.setContent(contactPhoto.getContent());
            photo.setContentType(contactPhoto.getContentType());
            photo.setName(contactPhoto.getName());
            return photo;
        }
    }
}
