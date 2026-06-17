package com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire;

import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;

import javax.persistence.*;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashSet;

@Entity
@Table(name = "PROFESSIONAL_QUESTIONNAIRES",
uniqueConstraints = {
    @UniqueConstraint(
        name = "QUESTIONNAIRE_PER_PROFESSIONAL_CATEGORY",
        columnNames = { "fk_professional_id", "fk_category_id"}
    )
})
public class ProfessionalQuestionnaire extends AuditedEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_professional_id", nullable = false, updatable = false)
    private Professional professional;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_category_id", nullable = false, updatable = false)
    private Category category;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_questionnaire_id", nullable = false)
    private Set<QuestionnaireItem> items = newHashSet();

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<QuestionnaireItem> getItems() {
        return items;
    }

    public void setItems(Set<QuestionnaireItem> items) {
        this.items = items;
    }
}
