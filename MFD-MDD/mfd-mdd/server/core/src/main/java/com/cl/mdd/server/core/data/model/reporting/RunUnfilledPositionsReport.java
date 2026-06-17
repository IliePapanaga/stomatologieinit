package com.cl.mdd.server.core.data.model.reporting;

import com.cl.mdd.server.core.data.model.reporting.commons.PositionType;
import com.cl.mdd.server.core.data.model.reporting.commons.RunReport;
import com.cl.mdd.server.core.validation.constraint.SubCategory;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

public class RunUnfilledPositionsReport extends RunReport {

    private LocalDate from;

    private LocalDate to;

    private PositionType positionType;

    @SubCategory
    private String requiredSubcategory;

    @Min(value = 0)
    @Max(value = Integer.MAX_VALUE)
    private Integer minUnfilledDays;

    @Min(value = 0)
    @Max(value = Integer.MAX_VALUE)
    private Integer maxUnfilledDays;

    private Double latitude;

    private Double longitude;

    private Double radius;

    @Nullable
    public LocalDate getFrom() {
        return from;
    }

    public RunUnfilledPositionsReport setFrom(LocalDate from) {
        this.from = from;
        return this;
    }

    @Nullable
    public LocalDate getTo() {
        return to;
    }

    public RunUnfilledPositionsReport setTo(LocalDate to) {
        this.to = to;
        return this;
    }

    @Nullable
    public PositionType getPositionType() {
        return positionType;
    }

    public RunUnfilledPositionsReport setPositionType(PositionType positionType) {
        this.positionType = positionType;
        return this;
    }

    @Nullable
    public String getRequiredSubcategory() {
        return requiredSubcategory;
    }

    public RunUnfilledPositionsReport setRequiredSubcategory(String requiredSubcategory) {
        this.requiredSubcategory = requiredSubcategory;
        return this;
    }

    @Nullable
    public Integer getMinUnfilledDays() {
        return minUnfilledDays;
    }

    public RunUnfilledPositionsReport setMinUnfilledDays(Integer minUnfilledDays) {
        this.minUnfilledDays = minUnfilledDays;
        return this;
    }

    @Nullable
    public Integer getMaxUnfilledDays() {
        return maxUnfilledDays;
    }

    public RunUnfilledPositionsReport setMaxUnfilledDays(Integer maxUnfilledDays) {
        this.maxUnfilledDays = maxUnfilledDays;
        return this;
    }

    @Nullable
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Nullable
    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
