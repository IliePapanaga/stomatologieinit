package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@ExpressionConstraint.List({
        @ExpressionConstraint(expression = "#this.startTime == null or #this.endTime == null or #this.startTime.isBefore(#this.endTime)",
                message = "{job.day.start.times.valid}"),
        @ExpressionConstraint(expression = "#this.startDate == null or #this.endDate == null or #this.startDate.isBefore(#this.endDate) or #this.startDate.equals(#this.endDate)",
                message = "{job.posting.start.dates.valid}")
})
public class PublishSimpleTemporaryJobPosting extends PublishTemporaryJobPosting {

    @NotNull(message = "{job.posting.start.time.not.null}")
    private LocalTime startTime;

    @NotNull(message = "{job.posting.end.time.not.null}")
    private LocalTime endTime;

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

}
