package com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.items;

import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.QuestionnaireItem;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("NUMBER")
public class NumberItem extends QuestionnaireItem {

    @Column(name = "number")
    private Integer number;

    public NumberItem() {
    }

    public NumberItem(String key, Integer number) {
        super(key);
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
