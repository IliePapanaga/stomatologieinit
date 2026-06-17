package com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.*;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Table(name = "PROFESSIONAL_QUESTIONNAIRE_ITEMS",
        uniqueConstraints = {
                @UniqueConstraint(name = "ITEM_IN_QUESTIONNAIRE", columnNames = {
                        "item_key", "fk_questionnaire_id"
                })
        })
@Inheritance(strategy = SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "ITEM_TYPE")
public abstract class QuestionnaireItem extends Identifiable {

    @Column(name = "item_key", nullable = false)
    private String key;

    public QuestionnaireItem() {
    }

    public QuestionnaireItem(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
