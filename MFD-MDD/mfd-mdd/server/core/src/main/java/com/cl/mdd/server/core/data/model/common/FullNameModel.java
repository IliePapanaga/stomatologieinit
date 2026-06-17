package com.cl.mdd.server.core.data.model.common;

import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.composite.FirstName;
import com.cl.mdd.server.core.validation.constraint.composite.LastName;
import com.cl.mdd.server.core.validation.group.Register;
import com.cl.mdd.server.core.validation.group.Save;
import com.cl.mdd.server.core.validation.group.Update;

import javax.validation.constraints.NotNull;

public class FullNameModel extends MDDModel {

    private String title;

    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{name.first.not.null}")
    @FirstName
    private String first;

    private String middle;

    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{name.last.not.null}")
    @LastName
    private String last;

    public FullNameModel() {
    }

    public FullNameModel(String first, String last) {
        this.first = first;
        this.last = last;
    }

    public String getTitle() {
        return title;
    }

    public FullNameModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getFirst() {
        return first;
    }

    public FullNameModel setFirst(String first) {
        this.first = first;
        return this;
    }

    public String getMiddle() {
        return middle;
    }

    public FullNameModel setMiddle(String middle) {
        this.middle = middle;
        return this;
    }

    public String getLast() {
        return last;
    }

    public FullNameModel setLast(String last) {
        this.last = last;
        return this;
    }
}
