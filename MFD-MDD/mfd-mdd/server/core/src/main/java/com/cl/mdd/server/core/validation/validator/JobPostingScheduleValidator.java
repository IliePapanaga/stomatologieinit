package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.model.WorkScheduleModel;
import com.cl.mdd.server.core.validation.constraint.JobPostingSchedule;
import com.google.common.collect.Sets;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class JobPostingScheduleValidator implements ConstraintValidator<JobPostingSchedule, List<WorkScheduleModel>> {

    @Override
    public void initialize(JobPostingSchedule constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<WorkScheduleModel> value, ConstraintValidatorContext context) {
        return isEmpty(value) || isValid(value);
    }

    public boolean isValid(List<WorkScheduleModel> workScheduleModels) {
        List<String> weekDays = workScheduleModels.stream().map(WorkScheduleModel::getWeekDay).collect(Collectors.toList());
        return Sets.newHashSet(weekDays).size() == weekDays.size();
    }

}
