package com.cl.mdd.server.core.data.persistent.model.specialty;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Entity
@Table(name = "SUB_CATEGORIES")
public class SubCategory extends Identifiable {

    public static final String REQUIRES_REVIEW = "REQUIRES_REVIEW";

    public static final String PENDING = "PENDING";

    public static final String REJECTED = "REJECTED";

    public static final String EXPIRED = "EXPIRED";

    public static final String APPROVED = "APPROVED";

    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "fk_category_id", updatable = false, nullable = false)
    private Category category;

    /**
     * Contains all subcategories, and subcategories of these subcategories.
     * <p>
     * If A > B and B > C.
     * Then A.comprisedSubcategories will contain both B and C.
     */
    @ManyToMany
    @JoinTable(name = "SUBCATEGORY_COMPRISED_SUBCATEGORIES",
            joinColumns = @JoinColumn(name = "fk_subcategory_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_comprised_subcategory_id"))
    private Set<SubCategory> comprisedSubcategories;

    @ManyToMany
    @JoinTable(name = "SUBCATEGORY_TO_CERTIFICATE_TYPE",
            joinColumns = {@JoinColumn(name = "fk_subcategory_id")},
            inverseJoinColumns = {@JoinColumn(name = "fk_certificate_type_id")})
    private Set<CertificateType> certificateTypes = newHashSet();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<SubCategory> getComprisedSubcategories() {
        return comprisedSubcategories;
    }

    public SubCategory setComprisedSubcategories(Set<SubCategory> comprisedSubcategories) {
        this.comprisedSubcategories = comprisedSubcategories;
        return this;
    }

    public Set<CertificateType> getCertificateTypes() {
        return certificateTypes;
    }


    public void setCertificateTypes(Set<CertificateType> certificateTypes) {
        this.certificateTypes = certificateTypes;
    }

}
