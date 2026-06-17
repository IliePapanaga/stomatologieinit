package com.cl.mdd.server.core.data.model;

import com.cl.mdd.server.core.validation.constraint.JobPostingSchedule;
import com.cl.mdd.server.core.validation.constraint.posting.WeeklyDaysSchedule;
import com.cl.mdd.server.core.validation.group.Complexity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@WeeklyDaysSchedule(groups = Complexity.High.class)
public class PublishWeeklyTemporaryJobPosting extends PublishTemporaryJobPosting {

    @Valid
    @NotEmpty(message = "{job.posting.work.schedules.not.empty}")
    @JobPostingSchedule(message = "{job.posting.work.schedules.unique}")
    private List<WorkScheduleModel> workSchedules;

    public List<WorkScheduleModel> getWorkSchedules() {
        return workSchedules;
    }

    public PublishWeeklyTemporaryJobPosting setWorkSchedules(List<WorkScheduleModel> workSchedules) {
        this.workSchedules = workSchedules;
        return this;
    }
}
