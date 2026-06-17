package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class FormUrlResponse {

    @XmlElement(name = "result")
    private Integer result;

    @XmlElement(name = "result-text")
    private String resultText;

    @XmlElement(name = "form-url")
    private String formUrl;

    public Integer getResult() {
        return this.result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getResultText() {
        return this.resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public String getFormUrl() {
        return this.formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }
}
