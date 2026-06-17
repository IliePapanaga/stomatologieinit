package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.model.JobInterviewScheduleOption;
import com.cl.mdd.server.core.data.model.ScheduleJobInterview;
import com.cl.mdd.server.core.data.persistent.access.posting.PermanentJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.JobInterviewValidOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Validator
public class JobInterviewValidOptionsValidatorForScheduleJobInterview implements ConstraintValidator<JobInterviewValidOptions, ScheduleJobInterview> {

    @Autowired
    private PermanentJobPostingApplicationDao permanentJobPostingApplicationDao;

    @Override
    public void initialize(JobInterviewValidOptions constraintAnnotation) {
    }

    @Override
    public boolean isValid(ScheduleJobInterview interview, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(interview.getApplicationId())) {
            return true;
        }

        PermanentJobPosting permanentJobPosting = permanentJobPostingApplicationDao.findOne(interview.getApplicationId()).getPermanentJobPosting();

        ZonedDateTime maxDateForInterview = permanentJobPosting.getZonedStartDateTime();
        ZoneId timeZone = permanentJobPosting.getLocation().getTimeZone();

        for (JobInterviewScheduleOption interviewOption : interview.getOptions()) {
            ZonedDateTime optionDate = ZonedDateTime.of(interviewOption.getDate(), interviewOption.getTime(), timeZone);
            if (optionDate.isAfter(maxDateForInterview)) {
                return false;
            }
        }
        return true;
    }
}
