package com.cl.mdd.server.core.data.persistent.model.posting;

import com.cl.mdd.server.core.data.persistent.listeners.AbsoluteStartTimeAdjuster;
import com.cl.mdd.server.core.data.persistent.model.common.AuditedEntity;
import com.cl.mdd.server.core.data.persistent.model.common.HasAbsoluteStartTime;
import com.cl.mdd.server.core.data.persistent.model.common.Language;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.WorkSchedule;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.google.common.collect.Sets;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Table(name = "JOB_POSTINGS")
@Inheritance(strategy = SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "JOB_POSTING_TYPE")
@EntityListeners(AbsoluteStartTimeAdjuster.class)
public abstract class JobPosting extends AuditedEntity implements HasAbsoluteStartTime {

    public static final String CANCELLED = "CANCELLED";

    public static final String DELETED = "DELETED";

    public static final String ACTIVE = "ACTIVE";

    public static final String FILLED = "FILLED";

    public static final String UNDER_REVIEW = "UNDER_REVIEW";

    public static final String PARTIALLY_FILLED = "PARTIALLY_FILLED";

    public static final String REJECTED = "REJECTED";

    public static final String COMPLETED = "COMPLETED";

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "comment")
    private String comment;

    @ManyToMany
    @JoinTable(name = "JOB_POSTING_SUB_CATEGORIES",
            joinColumns = @JoinColumn(name = "fk_job_posting_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_sub_category_id"))
    private Set<SubCategory> subCategories = newHashSet();

    @ManyToMany
    @JoinTable(name = "JOB_POSTING_LANGUAGES",
            joinColumns = @JoinColumn(name = "fk_job_posting_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_language_id"))
    private Set<Language> languages = newHashSet();


    @ManyToOne
    @JoinColumn(name = "fk_practice_location_id", nullable = false)
    private PracticeLocation location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fk_job_posting_id")
    private Set<WorkSchedule> workSchedules = Sets.newHashSet();

    @ManyToOne
    @JoinColumn(name = "fk_preferred_professional_id")
    private Professional preferredProfessional;

    @Column(name = "zoned_start_date_time")
    private ZonedDateTime zonedStartDateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    public PracticeLocation getLocation() {
        return location;
    }

    public void setLocation(PracticeLocation location) {
        this.location = location;
    }

    public Set<WorkSchedule> getWorkSchedules() {
        return workSchedules;
    }

    public JobPosting setWorkSchedules(Set<WorkSchedule> workSchedules) {
        this.workSchedules = workSchedules;
        return this;
    }

    public Professional getPreferredProfessional() {
        return preferredProfessional;
    }

    public JobPosting setPreferredProfessional(Professional preferredProfessional) {
        this.preferredProfessional = preferredProfessional;
        return this;
    }


    @Override
    public ZoneId getTimeZone() {
        return this.getLocation().getTimeZone();
    }

    @Override
    public void setZonedStartDateTime(ZonedDateTime zonedStartDateTime) {
        this.zonedStartDateTime = zonedStartDateTime;
    }

    @Override
    public ZonedDateTime getZonedStartDateTime() {
        return zonedStartDateTime;
    }
}
