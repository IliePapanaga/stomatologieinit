package com.cl.mdd.server.core.data.persistent.model.specialty;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CERTIFICATE_TYPES")
public class CertificateType extends Identifiable {

    // certificate types
    public static final String DAC = "DAC";
    public static final String XRAY = "XRAY";
    public static final String CDA = "CDA";
    public static final String CPR = "CPR";
    public static final String RDA = "RDA";
    public static final String RDAEF = "RDAEF";
    public static final String RDAEF_1 = "RDAEF1";
    public static final String RDAEF_2 = "RDAEF2";
    public static final String RDH = "RDH";
    public static final String RDHAP = "RDHAP";
    public static final String LIABILITY ="LIABILITY";
    public static final String DIODE_LASER = "DIODE_LASER";
    public static final String DDS_OR_DMD = "DDS_OR_DMD";
    public static final String DEA = "DEA";
    public static final String NPI = "NPI";
    public static final String ENDODONTIC_ASSISTANT = "ENDODONTIC_ASSISTANT";
    public static final String ORAL_SURGERY_ASSISTANT = "ORAL_SURGERY_ASSISTANT";
    public static final String ORTHODONTIC_ASSISTANT = "ORTHODONTIC_ASSISTANT";
    public static final String PEDODONTIC_ASSISTANT = "PEDODONTIC_ASSISTANT";
    public static final String PERIODONTAL_ASSISTANT = "PERIODONTAL_ASSISTANT";

    @Column(name = "optional", updatable = false)
    private boolean optional;

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
