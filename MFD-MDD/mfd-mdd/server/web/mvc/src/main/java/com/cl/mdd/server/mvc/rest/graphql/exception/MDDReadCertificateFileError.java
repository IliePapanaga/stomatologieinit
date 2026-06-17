package com.cl.mdd.server.mvc.rest.graphql.exception;

import com.cl.mdd.server.core.exception.MDDException;

public class MDDReadCertificateFileError extends MDDException {

    private static final String message = "Unable to read certificate content";
    private static final String code = "CERTIFICATE_FILE_IO_EXCEPTION";

    public MDDReadCertificateFileError(Throwable cause) {
        super(message, cause, code);
    }
}
