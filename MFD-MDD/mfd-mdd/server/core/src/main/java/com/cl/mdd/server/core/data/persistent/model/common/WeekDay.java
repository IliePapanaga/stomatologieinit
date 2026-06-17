package com.cl.mdd.server.core.data.persistent.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "WEEK_DAYS")
public class WeekDay extends Identifiable {

    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;

    @Column(name = "index_number", unique = true, updatable = false, nullable = false)
    private int indexNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }
}
