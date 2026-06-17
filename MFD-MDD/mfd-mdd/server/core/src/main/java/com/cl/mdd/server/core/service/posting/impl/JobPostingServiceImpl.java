package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.geocoding.GeocodingArea;
import com.cl.mdd.server.core.data.model.query.*;
import com.cl.mdd.server.core.data.model.query.model.*;
import com.cl.mdd.server.core.data.persistent.access.posting.*;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.JobPostingCancelledEvent;
import com.cl.mdd.server.core.event.type.posting.JobPostingDeletedEvent;
import com.cl.mdd.server.core.event.type.posting.JobPostingPublishedEvent;
import com.cl.mdd.server.core.event.type.posting.JobPostingUpdatedEvent;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.geocoding.GeocodingUtils;
import com.cl.mdd.server.core.service.posting.JobPostingApplicationService;
import com.cl.mdd.server.core.service.posting.JobPostingService;
import com.cl.mdd.server.core.validation.group.Complexity;
import com.cl.mdd.server.core.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.persistent.model.posting.JobPosting.CANCELLED;
import static com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.iterate;

@Service
@Validated
public class JobPostingServiceImpl extends ServiceSupport implements JobPostingService {

    @Autowired
    private JobPostingApplicationService jobPostingApplicationService;

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private TemporaryJobPostingDao temporaryJobPostingDao;

    @Autowired
    private PermanentJobPostingDao permanentJobPostingDao;

    @Autowired
    private JobPostingDao jobPostingDao;

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private GeocodingUtils geocodingUtils;

    @Autowired
    private EventBus<JobPostingPublishedEvent> jobPostingPublishedEventBus;

    @Autowired
    private EventBus<JobPostingUpdatedEvent> jobPostingUpdatedEventBus;

    @Autowired
    private EventBus<JobPostingCancelledEvent> jobPostingCancelledEventBus;

    @Autowired
    private EventBus<JobPostingDeletedEvent> jobPostingDeletedEventBus;

    @Value("${professional.maxNoShowTolerance:2}")
    private int maxNoShowTolerance;

    @Value("${professional.maxRejectionsTolerance:5}")
    private int maxRejectionsTolerance;

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.readAllowed(#id)")
    public JobPosting get(String id) {
        if (isNull(id)) {
            return null;
        }
        return commonConverter.toJobPosting(jobPostingDao.findOne(id));
    }

    @Override
    @PreAuthorize("@practiceLocationAccessAuthorizer.publishJobPostingAllowed(#simpleTemporaryJobPosting.practiceLocationId)")
    @Validated(Complexity.class)
    public String publish(PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting) {
        TemporaryJobPosting posting = commonConverter.toNewActiveTemporaryJobPosting(simpleTemporaryJobPosting);
        posting.getJobDays().addAll(generateSimpleJobDays(simpleTemporaryJobPosting, posting));
        posting.setJobDayStrategy(JOB_DAY_STRATEGY_SIMPLE);
        posting.setStartTime(simpleTemporaryJobPosting.getStartTime());
        posting.setEndTime(simpleTemporaryJobPosting.getEndTime());
        posting.getWorkSchedules().clear();
        String id = saveTemporaryPosting(posting, nonNull(simpleTemporaryJobPosting.getPreferredCandidateId()));
        jobPostingPublishedEventBus.publishEvent(event -> event.setJobPostingId(id));
        return id;
    }

    @Override
    @PreAuthorize("@practiceLocationAccessAuthorizer.publishJobPostingAllowed(#weeklyTemporaryJobPosting.practiceLocationId)")
    @Validated(Complexity.class)
    public String publish(PublishWeeklyTemporaryJobPosting weeklyTemporaryJobPosting) {
        TemporaryJobPosting posting = commonConverter.toNewActiveTemporaryJobPosting(weeklyTemporaryJobPosting);
        posting.getJobDays().addAll(generateWeeklyJobDays(weeklyTemporaryJobPosting.getStartDate(), weeklyTemporaryJobPosting.getEndDate(), weeklyTemporaryJobPosting.getWorkSchedules(), posting));
        posting.setJobDayStrategy(JOB_DAY_STRATEGY_WEEKLY);
        posting.setWorkSchedules(weeklyTemporaryJobPosting.getWorkSchedules().stream().map(commonConverter::toWorkSchedule).collect(toSet()));
        String id = saveTemporaryPosting(posting, nonNull(weeklyTemporaryJobPosting.getPreferredCandidateId()));
        jobPostingPublishedEventBus.publishEvent(event -> event.setJobPostingId(id));
        return id;
    }

    @Override
    @PreAuthorize("@practiceLocationAccessAuthorizer.publishJobPostingAllowed(#complexTemporaryJobPosting.practiceLocationId)")
    @Validated(Complexity.class)
    public String publish(PublishComplexTemporaryJobPosting complexTemporaryJobPosting) {
        TemporaryJobPosting posting = commonConverter.toNewActiveTemporaryJobPosting(complexTemporaryJobPosting);
        posting.setJobDays(complexTemporaryJobPosting.getJobDays().stream().map(d -> commonConverter.toJobDay(d, posting)).collect(toSet()));
        posting.setJobDayStrategy(JOB_DAY_STRATEGY_COMPLEX);
        posting.getWorkSchedules().clear();
        String id = saveTemporaryPosting(posting, nonNull(complexTemporaryJobPosting.getPreferredCandidateId()));
        jobPostingPublishedEventBus.publishEvent(event -> event.setJobPostingId(id));
        return id;
    }

    @Override
    @PreAuthorize("@practiceLocationAccessAuthorizer.publishJobPostingAllowed(#simplePermanentJobPosting.practiceLocationId)")
    @Validated(Complexity.class)
    public String publish(PublishSimplePermanentJobPosting simplePermanentJobPosting) {
        PermanentJobPosting permanentJobPosting = commonConverter.toNewActivePermanentJobPosting(simplePermanentJobPosting);
        String id = savePermanentPosting(permanentJobPosting, permanentJobPosting.getPreferredProfessional() != null);
        jobPostingPublishedEventBus.publishEvent(event -> event.setJobPostingId(id));
        return id;
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.updateAllowed(#simpleTemporaryJobPosting.id)")
    @Validated(Update.class)
    public void update(SimpleTemporaryJobPosting simpleTemporaryJobPosting) {
        executeInTransaction(() -> {

            Set<JobDay> newJobDays = generateSimpleJobDays(simpleTemporaryJobPosting, temporaryJobPostingDao.getOne(simpleTemporaryJobPosting.getId()));

            handleJobDaysUpdate(newJobDays, simpleTemporaryJobPosting.getId());

            TemporaryJobPosting db = temporaryJobPostingDao.findOne(simpleTemporaryJobPosting.getId());

            commonConverter.toTemporaryJobPosting(simpleTemporaryJobPosting, db);

            db.setJobDayStrategy(JOB_DAY_STRATEGY_SIMPLE);
            db.getWorkSchedules().clear();
            db.setStartTime(simpleTemporaryJobPosting.getStartTime());
            db.setEndTime(simpleTemporaryJobPosting.getEndTime());
            temporaryJobPostingDao.save(db);
            userDao.updateLastActivityForCurrentUser();
        });

        jobPostingUpdatedEventBus.publishEvent(event -> event.setJobPostingId(simpleTemporaryJobPosting.getId()));
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.updateAllowed(#weeklyTemporaryJobPosting.id)")
    @Validated({Update.class, Complexity.class})
    public void update(WeeklyTemporaryJobPosting weeklyTemporaryJobPosting) {
        executeInTransaction(() -> {
            Set<JobDay> newJobDays = generateWeeklyJobDays(weeklyTemporaryJobPosting.getStartDate(), weeklyTemporaryJobPosting.getEndDate(), weeklyTemporaryJobPosting.getWorkSchedules(), temporaryJobPostingDao.getOne(weeklyTemporaryJobPosting.getId()));

            handleJobDaysUpdate(newJobDays, weeklyTemporaryJobPosting.getId());

            TemporaryJobPosting db = temporaryJobPostingDao.findOne(weeklyTemporaryJobPosting.getId());
            commonConverter.toTemporaryJobPosting(weeklyTemporaryJobPosting, db);

            db.setStartTime(null);
            db.setEndTime(null);
            db.setJobDayStrategy(JOB_DAY_STRATEGY_WEEKLY);
            db.getWorkSchedules().clear();
            db.getWorkSchedules().addAll(weeklyTemporaryJobPosting.getWorkSchedules().stream().map(commonConverter::toWorkSchedule).collect(toSet()));

            temporaryJobPostingDao.save(db);
            userDao.updateLastActivityForCurrentUser();
        });
        jobPostingUpdatedEventBus.publishEvent(event -> event.setJobPostingId(weeklyTemporaryJobPosting.getId()));
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.updateAllowed(#complexTemporaryJobPosting.id)")
    @Validated(Update.class)
    public void update(ComplexTemporaryJobPosting complexTemporaryJobPosting) {

        executeInTransaction(() -> {
            Set<JobDay> newJobDays = complexTemporaryJobPosting.getJobDays().stream().map(jobDayModel -> commonConverter.toJobDay(jobDayModel, temporaryJobPostingDao.getOne(complexTemporaryJobPosting.getId()))).collect(toSet());
            handleJobDaysUpdate(newJobDays, complexTemporaryJobPosting.getId());

            TemporaryJobPosting db = temporaryJobPostingDao.findOne(complexTemporaryJobPosting.getId());
            commonConverter.toTemporaryJobPosting(complexTemporaryJobPosting, db);

            db.setStartTime(null);
            db.setEndTime(null);
            db.setJobDayStrategy(JOB_DAY_STRATEGY_COMPLEX);
            db.getWorkSchedules().clear();
            temporaryJobPostingDao.save(db);
            userDao.updateLastActivityForCurrentUser();

        });
        jobPostingUpdatedEventBus.publishEvent(event -> event.setJobPostingId(complexTemporaryJobPosting.getId()));
    }

    private String saveTemporaryPosting(TemporaryJobPosting posting, boolean directBook) {
        return executeInTransaction(() -> {
            String jobPostingId = temporaryJobPostingDao.save(posting).getId();
            if (directBook) {
                jobPostingApplicationService.directBook(jobPostingId);
            }
            userDao.updateLastActivityForCurrentUser();
            return jobPostingId;
        });
    }

    private String savePermanentPosting(PermanentJobPosting posting, boolean hirePermanently) {
        return executeInTransaction(() -> {
            String jobPostingId = permanentJobPostingDao.save(posting).getId();
            if (hirePermanently) {
                jobPostingApplicationService.hirePermanently(jobPostingId);
            }
            userDao.updateLastActivityForCurrentUser();
            return jobPostingId;
        });
    }

    private void handleJobDaysUpdate(Set<JobDay> newJobDays, String id) {
        ZonedDateTime present = ZonedDateTime.now();

        List<LocalDate> newDates = newJobDays.stream().map(JobDay::getDate).collect(Collectors.toList());
        temporaryJobPostingApplicationDao.completePrematurelyOrCancelAllJobPostingApplications(id);
        jobDayDao.cancelAcceptedJobDaysFromAffectedApplications(id, present);
        jobDayDao.excludeOthersThan(id, newDates, present);

        newJobDays.forEach(jobDay -> {
            // if job day time is modified or it is excluded, such applications should be canceled.(or completed prematurely.)

            //merge
            JobDay dbJobDay = jobDayDao.findOneByJobPostingIdAndDate(id, jobDay.getDate());
            if (dbJobDay == null) {
                jobDayDao.save(jobDay);
            } else {
                if (dbJobDay.getZonedStartDateTime().isAfter(present) && wasModified(dbJobDay, jobDay)) {
                    dbJobDay.setStartTime(jobDay.getStartTime());
                    dbJobDay.setEndTime(jobDay.getEndTime());
                    dbJobDay.setExcluded(jobDay.isExcluded());
                    dbJobDay.setStatus(JobDay.NEW);
                    jobDayDao.save(dbJobDay);
                    temporaryJobPostingApplicationDao.completePrematurelyOrCancelAllJobPostingApplicationsAffectedByJobDay(id, jobDay.getId());
                }
            }
        });
    }

    private boolean wasModified(JobDay jobDay, JobDay newJobDay) {
        return !newJobDay.getStartTime().equals(jobDay.getStartTime()) || !newJobDay.getEndTime().equals(jobDay.getEndTime()) || (!jobDay.isExcluded() && newJobDay.isExcluded());
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.updateAllowed(#simplePermanentJobPosting.id)")
    public void update(SimplePermanentJobPosting simplePermanentJobPosting) {
        String updatedPostingId = executeInTransaction(() -> {
            PermanentJobPosting db = permanentJobPostingDao.findOne(simplePermanentJobPosting.getId());
            commonConverter.toPermanentJobPosting(simplePermanentJobPosting, db);
            db.getApplications().forEach(application -> application.setStatus(JobPostingApplication.CANCELLED_DURING_JOB_POSTING_UPDATE));
            permanentJobPostingDao.save(db);
            userDao.updateLastActivityForCurrentUser();
            return db.getId();
        });
        jobPostingUpdatedEventBus.publishEvent(event -> event.setJobPostingId(updatedPostingId));
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.updateAllowed(#id)")
    public void cancel(String id) {
        Boolean updated = executeInTransaction(() -> {
            boolean statusChanged = jobPostingDao.changeStatus(id, CANCELLED) != 0;
            if (statusChanged) {
                jobPostingApplicationService.cancelAll(id);
                userDao.updateLastActivityForCurrentUser();
            }
            return statusChanged;
        });
        if (updated) {
            jobPostingCancelledEventBus.publishEvent(event -> event.setJobPostingId(id));
        }
    }

    @Override
    @PreAuthorize("@jobPostingAccessAuthorizer.deleteAllowed(#id)")
    public void delete(String id) {
        Boolean updated = executeInTransaction(() -> jobPostingDao.changeStatus(id, DELETED) != 0);
        if (updated) {
            jobPostingDeletedEventBus.publishEvent(event -> event.setJobPostingId(id));
        }
    }

    @Override
    @PreAuthorize("@professionalAccessAuthorizer.readAllowed(#queryInfo?.filters?.professionalId)")
    public QueryResult<ProfessionalTemporaryJobPosting> fetch(ProfessionalTemporaryJobPostingQuery queryInfo) {
        ProfessionalTemporaryJobPostingQuery.ProfessionalTemporaryJobPostingFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        LocalDate startDate = filters.getStartDate();
        LocalDate endDate = filters.getEndDate();

        Page<ProfessionalTemporaryJobPostingTuple> professionalJobPostings = temporaryJobPostingDao.findProfessionalJobPostings(filters.getProfessionalId(),
                filters.getStatus(),
                startDate,
                endDate,
                ZonedDateTime.now(),
                maxNoShowTolerance,
                maxRejectionsTolerance,
                pageable);
        return queryConverter.toQueryResult(professionalJobPostings, commonConverter::toProfessionalTemporaryJobPosting);
    }

    @Override
    @PreAuthorize("@professionalAccessAuthorizer.readAllowed(#queryInfo?.filters?.professionalId)")
    public QueryResult<ProfessionalPermanentJobPosting> fetch(ProfessionalPermanentJobPostingQuery queryInfo) {
        ProfessionalPermanentJobPostingQuery.ProfessionalPermanentJobPostingFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        LocalDate startDateFrom = filters.getStartDate();

        Page<ProfessionalPermanentJobPostingTuple> professionalJobPostings = permanentJobPostingDao.findProfessionalJobPostings(filters.getProfessionalId(),
                filters.getStatus(),
                startDateFrom,
                maxNoShowTolerance,
                maxRejectionsTolerance,
                pageable);
        return queryConverter.toQueryResult(professionalJobPostings, commonConverter::toProfessionalPermanentJobPosting);
    }

    @Override
    @PreAuthorize("@practiceOwnerAccessAuthorizer.readAllowed(#queryInfo?.filters?.practiceOwnerId)")
    public QueryResult<PracticeOwnerTemporaryJobPosting> fetch(PracticeOwnerTemporaryJobPostingQuery queryInfo) {
        PracticeOwnerTemporaryJobPostingQuery.PracticeOwnerTemporaryJobPostingFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        LocalDate startDate = filters.getStartDate();
        LocalDate endDate = filters.getEndDate();

        ZonedDateTime now = ZonedDateTime.now();
        Page<PracticeOwnerTemporaryJobPostingTuple> professionalJobPostings = temporaryJobPostingDao.findPracticeOwnerJobPostings(filters.getPracticeOwnerId(),
                filters.getStatus(),
                startDate,
                endDate,
                now,
                pageable);
        return queryConverter.toQueryResult(professionalJobPostings, commonConverter::toPracticeOwnerTemporaryJobPosting);
    }

    @Override
    @PreAuthorize("@practiceOwnerAccessAuthorizer.readAllowed(#queryInfo?.filters?.practiceOwnerId)")
    public QueryResult<PracticeOwnerPermanentJobPosting> fetch(PracticeOwnerPermanentJobPostingQuery queryInfo) {
        PracticeOwnerPermanentJobPostingQuery.PracticeOwnerPermanentJobPostingFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        LocalDate startDateFrom = filters.getStartDate();
        Page<PracticeOwnerPermanentJobPostingTuple> professionalJobPostings = permanentJobPostingDao.findPracticeOwnerJobPostings(filters.getPracticeOwnerId(), filters.getStatus(), startDateFrom, pageable);
        return queryConverter.toQueryResult(professionalJobPostings, commonConverter::toPracticeOwnerPermanentJobPosting);
    }

    @Override
    @RequiresSystemUserRole
    public QueryResult<SystemUserTemporaryJobPosting> fetch(SystemUserTemporaryJobPostingQuery queryInfo) {
        SystemUserTemporaryJobPostingQuery.SystemUserTemporaryJobPostingFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        LocalDate startDate = filters.getStartDate();
        LocalDate endDate = filters.getEndDate();

        GeocodingArea area = geocodingUtils.toSquaredArea(filters.getDistance(), filters.getLat(), filters.getLng());

        Page<SystemUserTemporaryJobPostingTuple> systemUserJobPostings = temporaryJobPostingDao.findSystemUserJobPostings(filters.getStatus(),
                startDate,
                endDate, area.getDistance(), area.getLat(), area.getLng(),
                area.getLat1(), area.getLat2(), area.getLng1(), area.getLng2(),
                filters.getSpecialties(),
                pageable);
        return queryConverter.toQueryResult(systemUserJobPostings, commonConverter::toSystemUserTemporaryJobPosting);
    }

    @Override
    @RequiresSystemUserRole
    public QueryResult<SystemUserPermanentJobPosting> fetch(SystemUserPermanentJobPostingQuery queryInfo) {
        SystemUserPermanentJobPostingQuery.SystemUserPermanentJobPostingFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        LocalDate startDateFrom = filters.getStartDate();
        GeocodingArea area = geocodingUtils.toSquaredArea(filters.getDistance(), filters.getLat(), filters.getLng());

        Page<SystemUserPermanentJobPostingTuple> systemUserJobPostings = permanentJobPostingDao.findSystemUserJobPostings(filters.getStatus(), startDateFrom,
                area.getDistance(), area.getLat(), area.getLng(),
                area.getLat1(), area.getLat2(), area.getLng1(), area.getLng2(), filters.getSpecialties(), pageable);
        return queryConverter.toQueryResult(systemUserJobPostings, commonConverter::toSystemUserPermanentJobPosting);
    }

    private Set<JobDay> generateSimpleJobDays(PublishSimpleTemporaryJobPosting simpleTemporaryJobPosting, TemporaryJobPosting one) {
        LocalDate startDate = simpleTemporaryJobPosting.getStartDate();
        LocalDate endDate = simpleTemporaryJobPosting.getEndDate();
        return iterate(startDate, localDate -> localDate.plusDays(1))
                .limit(startDate.until(endDate, ChronoUnit.DAYS) + 1) // +1 means inclusive
                .map(localDate -> {
                    JobDay jobDay = new JobDay();
                    jobDay.setJobPosting(one);
                    jobDay.setDate(localDate);
                    jobDay.setStartTime(simpleTemporaryJobPosting.getStartTime());
                    jobDay.setEndTime(simpleTemporaryJobPosting.getEndTime());
                    return jobDay;
                }).collect(toSet());
    }

    private Set<JobDay> generateWeeklyJobDays(LocalDate startDate, LocalDate endDate, List<WorkScheduleModel> scheduleModels, TemporaryJobPosting posting) {
        Set<String> weekDays = scheduleModels.stream().map(WorkScheduleModel::getWeekDay).collect(toSet());

        return iterate(startDate, localDate -> localDate.plusDays(1))
                .limit(startDate.until(endDate, ChronoUnit.DAYS) + 1) // +1 means inclusive
                .filter(localDate -> weekDays.contains(localDate.getDayOfWeek().toString()))
                .map(localDate -> {
                    JobDay jobDay = new JobDay();
                    jobDay.setJobPosting(posting);
                    jobDay.setDate(localDate);
                    DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                    WorkScheduleModel dayOfWeekSchedule = scheduleModels.stream().filter(workScheduleModel -> workScheduleModel.getWeekDay().equals(dayOfWeek.name())).findFirst().get();
                    jobDay.setStartTime(dayOfWeekSchedule.getStartTime());
                    jobDay.setEndTime(dayOfWeekSchedule.getEndTime());
                    return jobDay;
                }).collect(toSet());
    }

}
