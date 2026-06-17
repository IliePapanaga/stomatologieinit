package com.cl.mdd.server.core.data.persistent.model.common.embeddable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FullName {

    @Column(name = "name_title")
    private String title;

    @Column(name = "name_first", nullable = false)
    private String first;

    @Column(name = "name_middle")
    private String middle;

    @Column(name = "name_last", nullable = false)
    private String last;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
