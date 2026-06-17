package com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.items;

import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.QuestionnaireItem;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TEXT")
public class TextItem extends QuestionnaireItem {

    @Column(name = "text", length = 1000)
    private String text;

    public TextItem() {
    }

    public TextItem(String key, String text) {
        super(key);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
