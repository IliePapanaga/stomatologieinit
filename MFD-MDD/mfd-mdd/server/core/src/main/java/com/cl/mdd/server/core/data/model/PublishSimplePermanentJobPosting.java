package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.JobPostingSchedule;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

public class PublishSimplePermanentJobPosting extends PublishAbstractJobPosting {

    @Valid
    @NotEmpty(message = "{job.posting.work.schedules.not.empty}")
    @JobPostingSchedule(message = "{job.posting.work.schedules.unique}")
    private List<WorkScheduleModel> workSchedules;

    public List<WorkScheduleModel> getWorkSchedules() {
        return workSchedules;
    }

    public PublishSimplePermanentJobPosting setWorkSchedules(List<WorkScheduleModel> workSchedules) {
        this.workSchedules = workSchedules;
        return this;
    }
}
