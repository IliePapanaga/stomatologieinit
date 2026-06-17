package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.validation.constraint.File;

import static com.cl.mdd.server.core.validation.constraint.File.FileType.IMAGE;

public class ContactPhotoModel {

    private String name;

    private String contentType;

    @File(allowedTypes = IMAGE, maxSize = 500L)
    private byte[] content;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
