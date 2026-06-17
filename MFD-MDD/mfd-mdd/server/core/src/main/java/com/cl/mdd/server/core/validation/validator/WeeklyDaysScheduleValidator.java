package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.model.PublishWeeklyTemporaryJobPosting;
import com.cl.mdd.server.core.data.model.WorkScheduleModel;
import com.cl.mdd.server.core.validation.annotation.Validator;
import com.cl.mdd.server.core.validation.constraint.posting.WeeklyDaysSchedule;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Validator
public class WeeklyDaysScheduleValidator implements ConstraintValidator<WeeklyDaysSchedule, PublishWeeklyTemporaryJobPosting> {

    @Override
    public void initialize(WeeklyDaysSchedule weeklyDaysSchedule) {
    }

    @Override
    public boolean isValid(PublishWeeklyTemporaryJobPosting value, ConstraintValidatorContext context) {
        return Objects.nonNull(value) && isValid(value);
    }

    private boolean isValid(PublishWeeklyTemporaryJobPosting weeklyTemporaryJobPosting) {
        LocalDate startDate = weeklyTemporaryJobPosting.getStartDate();
        LocalDate endDate = weeklyTemporaryJobPosting.getEndDate();
        long days = getDaysBetween(startDate, endDate);
        if (days >= 7) {
            return true;
        }
        List<DayOfWeek> daysOfWeek = IntStream.iterate(0, i -> i + 1)
                .limit(days)
                .mapToObj(i -> startDate.plusDays(i).getDayOfWeek())
                .collect(Collectors.toList());


        List<WorkScheduleModel> workSchedules = weeklyTemporaryJobPosting.getWorkSchedules();
        return workSchedules.stream().map(model -> DayOfWeek.valueOf(model.getWeekDay())).allMatch(daysOfWeek::contains);
    }

    // inclusive
    private long getDaysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end.plusDays(1));
    }
}
