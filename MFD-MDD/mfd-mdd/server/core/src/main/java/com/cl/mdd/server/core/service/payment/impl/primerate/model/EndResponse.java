package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class EndResponse {

    public static final String RESULT_OK = "1";

    @XmlElement(name = "result")
    private String result;

    @XmlElement(name = "result-text")
    private String resultText;

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultText() {
        return this.resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
}
