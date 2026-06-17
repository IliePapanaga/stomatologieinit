package com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.items;

import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.QuestionnaireItem;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("YES_NO")
public class YesNoItem extends QuestionnaireItem {

    @Column(name = "yes_no")
    private Boolean yes;

    public YesNoItem() {
    }

    public YesNoItem(String key, Boolean yes) {
        super(key);
        this.yes = yes;
    }

    public Boolean isYes() {
        return yes;
    }

    public void setYes(Boolean yes) {
        this.yes = yes;
    }
}
