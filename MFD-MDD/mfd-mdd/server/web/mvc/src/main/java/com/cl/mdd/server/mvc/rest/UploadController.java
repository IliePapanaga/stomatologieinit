package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.common.ContactPhotoModel;
import com.cl.mdd.server.core.exception.MDDException;
import com.cl.mdd.server.core.service.contact.ContactService;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1/")
public class UploadController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private WebSecurityAccess webSecurityAccess;

    @RequestMapping(value = "/user/contact-photo/upload", method = RequestMethod.POST)
    public void uploadUserContactPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        ContactPhotoModel photo = new ContactPhotoModel();
        photo.setName(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setContent(file.getBytes());
        contactService.updateUserPhoto(webSecurityAccess.currentUserId(), photo);
    }

    @GetMapping("/user/{id}/contact-photo")
    public void view(@PathVariable("id") String id, HttpServletResponse response) {
        viewPhoto(id, response);
    }

    private void viewPhoto(String id, HttpServletResponse response) {
        ContactPhotoModel photo = contactService.getUserPhoto(id);
        if (nonNull(photo)) {
            response.setContentType(photo.getContentType());
            response.setHeader("Content-Disposition", "inline" + "; filename=" + photo.getName());
            response.setContentLength(photo.getContent().length);
            copyContentToResponse(photo.getContent(), response);
        }
    }

    private void copyContentToResponse(byte[] content, HttpServletResponse response) {
        if (content == null) {
            return;
        }
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(content);
            FileCopyUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            throw new MDDException("Unable to load certificate content", "FILE_IO_EXCEPTION");
        }
    }

}
