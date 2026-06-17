package com.cl.mdd.server.core.data.model;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class PublishTemporaryJobPosting extends PublishAbstractJobPosting {

    @NotNull(message = "{job.posting.end.date.not.null}")
    private LocalDate endDate;

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
