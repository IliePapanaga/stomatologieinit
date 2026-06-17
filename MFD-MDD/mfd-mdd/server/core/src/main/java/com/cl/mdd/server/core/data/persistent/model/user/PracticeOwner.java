package com.cl.mdd.server.core.data.persistent.model.user;

import com.cl.mdd.server.core.data.persistent.model.practice.Practice;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import static javax.persistence.CascadeType.REMOVE;

@Entity
@DiscriminatorValue("PRACTICE_OWNER")
public class PracticeOwner extends User {

    @OneToOne(mappedBy = "owner", cascade = REMOVE)
    private Practice practice;

    public Practice getPractice() {
        return practice;
    }

    public PracticeOwner setPractice(Practice practice) {
        this.practice = practice;
        return this;
    }

    @Column(name = "comments", length = 1000)
    private String comments;

    public String getComments() {
        return comments;
    }

    public PracticeOwner setComments(String comments) {
        this.comments = comments;
        return this;
    }
}
