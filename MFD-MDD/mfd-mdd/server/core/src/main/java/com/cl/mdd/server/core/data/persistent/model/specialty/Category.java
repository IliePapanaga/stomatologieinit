package com.cl.mdd.server.core.data.persistent.model.specialty;

import com.cl.mdd.server.core.data.persistent.model.common.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Entity
@Table(name = "CATEGORIES")
public class Category extends Identifiable {

    // Category types
    public static final String DENTISTS = "DENTISTS";
    public static final String ASSISTANTS = "ASSISTANTS";
    public static final String HYGIENISTS = "HYGIENISTS";
    public static final String FRONT_OFFICE_PERSONNEL = "FRONT_OFFICE_PERSONNEL";

    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<SubCategory> subCategories = newHashSet();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
