package com.cl.mdd.server.core.event.type;

import com.cl.mdd.server.core.event.Event;

public class CertificateReviewedEvent extends Event {

    private String certificateDetailsId;

    public String getCertificateDetailsId() {
        return certificateDetailsId;
    }

    public void setCertificateDetailsId(String certificateDetailsId) {
        this.certificateDetailsId = certificateDetailsId;
    }

    @Override
    public void clear() {
        super.clear();
        this.certificateDetailsId = null;
    }
}
