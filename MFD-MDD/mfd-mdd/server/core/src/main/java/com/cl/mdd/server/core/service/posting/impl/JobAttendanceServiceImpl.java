package com.cl.mdd.server.core.service.posting.impl;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.FindProfessionalAttendances;
import com.cl.mdd.server.core.data.model.query.FindProfessionalNoShows;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.model.AttendanceTuple;
import com.cl.mdd.server.core.data.persistent.access.posting.*;
import com.cl.mdd.server.core.data.persistent.access.user.EmployeeRejectedDao;
import com.cl.mdd.server.core.data.persistent.access.user.NoShowDao;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.user.professional.*;
import com.cl.mdd.server.core.event.bus.EventBus;
import com.cl.mdd.server.core.event.type.posting.attendance.*;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.security.annotation.RequiresPracticeOwnerRole;
import com.cl.mdd.server.core.security.annotation.RequiresSystemUserRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.posting.JobAttendanceService;
import com.cl.mdd.server.core.validation.group.Save;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay.CHECKED_IN;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Validated
public class JobAttendanceServiceImpl extends ServiceSupport implements JobAttendanceService {

    @Autowired
    private JobDayDao jobDayDao;

    @Autowired
    private NoShowDao noShowDao;

    @Autowired
    private EmployeeRejectedDao employeeRejectedDao;

    @Autowired
    private ProfessionalManager professionalManager;

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private AttendanceAlertDao attendanceAlertDao;

    @Autowired
    private AttendanceAlertReplyDao attendanceAlertReplyDao;

    @Autowired
    private CheckInDao checkInDao;

    @Autowired
    private SosRequestDao sosRequestDao;

    @Autowired
    private EventBus<AttendanceAlertedEvent> attendanceAlertedEventEventBus;

    @Autowired
    private EventBus<AttendanceAlertRepliedEvent> attendanceAlertRepliedEventEventBus;

    @Autowired
    private EventBus<AttendanceSosRequestedEvent> attendanceSosRequestedEventEventBus;

    @Autowired
    private EventBus<AttendanceCheckedInEvent> attendanceCheckedInEventEventBus;

    @Autowired
    private EventBus<AttendanceNoShowEvent> attendanceNoShowEventEventBus;

    @Autowired
    private EventBus<AttendanceRejectedEvent> attendanceRejectedEventEventBus;

    @Override
    @PreAuthorize("@jobDayAccessAuthorizer.checkInAllowed(#attendance.jobDayId)")
    public void checkIn(CheckInAttendance attendance) {
        internalCheckIn(attendance);
    }

    @Override
    public void internalCheckIn(CheckInAttendance attendance) {
        if (noShowDao.existsById(attendance.getJobDayId())) {
            return;
        }

        String checkInId = executeInTransaction(() -> {
            String jobDayId = attendance.getJobDayId();
            TemporaryJobPostingApplication application = temporaryJobPostingApplicationDao.findOneByAttendanceId(jobDayId);
            if (isNull(application)) {
                return null;
            }

            JobDay jobDay = jobDayDao.findOne(jobDayId);

            CheckIn checkIn = new CheckIn();
            checkIn.setProfessional(application.getProfessional());
            checkIn.setJobDay(jobDay);
            CheckIn saved = checkInDao.save(checkIn);


            jobDay.setStatus(CHECKED_IN);
            jobDayDao.save(jobDay);
            userDao.updateLastActivityForCurrentUser();
            return saved.getId();
        });

        if (nonNull(checkInId)) {
            attendanceCheckedInEventEventBus.publishEvent(event -> event.setCheckInId(checkInId));
        }
    }

    @Override
    @PreAuthorize("@jobDayAccessAuthorizer.updateAllowed(#alertAttendance.jobDayId)")
    public void alert(AlertAttendance alertAttendance) {
        String alertId = executeInTransaction(() -> {
            String jobDayId = alertAttendance.getJobDayId();
            TemporaryJobPostingApplication application = temporaryJobPostingApplicationDao.findOneByAttendanceId(jobDayId);
            if (isNull(application)) {
                return null;
            }

            JobDay jobDay = jobDayDao.findOne(jobDayId);

            AttendanceAlert attendanceAlert = new AttendanceAlert();
            attendanceAlert.setProfessional(application.getProfessional());
            attendanceAlert.setJobDay(jobDay);
            AttendanceAlert saved = attendanceAlertDao.save(attendanceAlert);

            jobDay.setAlerted(true);
            jobDayDao.save(jobDay);
            return saved.getId();
        });
        if (nonNull(alertId)) {
            attendanceAlertedEventEventBus.publishEvent(event -> event.setAlertId(alertId));
        }
    }

    @Override
    @PreAuthorize("@jobDayAccessAuthorizer.updateAllowed(#requestAttendanceSos.jobDayId)")
    public void requestSos(RequestAttendanceSos requestAttendanceSos) {
        String sosId = executeInTransaction(() -> {
            String jobDayId = requestAttendanceSos.getJobDayId();
            TemporaryJobPostingApplication application = temporaryJobPostingApplicationDao.findOneByAttendanceId(jobDayId);
            if (isNull(application)) {
                return null;
            }

            SosRequest sosRequest = new SosRequest();
            sosRequest.setProfessional(application.getProfessional());
            JobDay jobDay = jobDayDao.findOne(jobDayId);
            sosRequest.setJobDay(jobDay);

            SosRequest saved = sosRequestDao.save(sosRequest);

            jobDay.setSosRequested(true);
            jobDayDao.save(jobDay);
            return requestAttendanceSos.isNoShow() ? addNoShow(new AddNoShowModel(requestAttendanceSos.getJobDayId())).getId() : saved.getId();
        });

        if (nonNull(sosId)) {
            attendanceSosRequestedEventEventBus.publishEvent(event -> event.setSosRequestId(sosId));
        }
    }

    @Override
    @Transactional
    @RequiresSystemUserRole
    public void dismissSos(DismissSos dismissSos) {
        jobDayDao.dismissSos(dismissSos.getJobPostingId());
    }

//    @Override
//    @Transactional(readOnly = true)
//    @RequiresPracticeOwnerRole
//    public List<Attendance> attendances(FindProfessionalAttendances query) {
//        List<AttendanceTuple> attendances = jobDayDao.findAttendances(query.getFilters().getPracticeOwnerId(), query.getFilters().getLocalDate());
//        return attendances.stream().map(commonConverter::toAttendance).collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    @RequiresPracticeOwnerRole
    public QueryResult<Attendance> attendances(FindProfessionalAttendances query) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime from = now.minusDays(1);
        ZonedDateTime to = now.plusDays(1);
        Pageable pageable = queryConverter.toPageable(query.getPagination());
        Page<AttendanceTuple> attendances = jobDayDao.findAttendances(query.getFilters().getPracticeOwnerId(), from, to, pageable);
        return queryConverter.toQueryResult(attendances, commonConverter::toAttendance);
    }

    @Override
    @Validated(Save.class)
    @PreAuthorize("@jobDayAccessAuthorizer.updateAllowed(#noShowModel.jobDayId)")
    public NoShowModel addNoShow(AddNoShowModel noShowModel) {
        NoShow noShow = executeInTransaction(() -> {
            TemporaryJobPostingApplication temporaryJobPostingApplication = temporaryJobPostingApplicationDao.findOneByAttendanceId(noShowModel.getJobDayId());
            if (isNull(temporaryJobPostingApplication)) {
                return null;
            }

            // audit change no show
            NoShow toBeSaved = new NoShow();
            toBeSaved.setProfessional(temporaryJobPostingApplication.getProfessional());
            toBeSaved.setJobDay(jobDayDao.getOne(noShowModel.getJobDayId()));
            toBeSaved.setStatus(NoShow.REGISTERED);
            toBeSaved.setModified(ZonedDateTime.now());

            NoShow noshow = noShowDao.saveAndFlush(toBeSaved);
            recalculateProNoShows(temporaryJobPostingApplication.getProfessional());
            userDao.updateLastActivityForCurrentUser();
            return noshow;
        });

        if (nonNull(noShow)) {
            attendanceNoShowEventEventBus.publishEvent(event -> event.setNoShowId(noShowModel.getJobDayId()));
        }
        return commonConverter.toNoShowModel(noShow);
    }

    @Override
    @PreAuthorize("@jobDayAccessAuthorizer.updateAllowed(#rejectEmployeeModel.jobDayId)")
    public void rejectEmployee(RejectEmployeeModel rejectEmployeeModel) {
        TemporaryJobPostingApplication temporaryJobPostingApplication = temporaryJobPostingApplicationDao.findOneByAttendanceId(rejectEmployeeModel.getJobDayId());
        if (isNull(temporaryJobPostingApplication)) {
            return;
        }
        EmployeeRejected employeeRejected = new EmployeeRejected();
        employeeRejected.setProfessional(temporaryJobPostingApplication.getProfessional());
        employeeRejected.setJobDay(jobDayDao.getOne(rejectEmployeeModel.getJobDayId()));
        employeeRejected.setStatus(NoWork.REGISTERED);
        employeeRejected.setComments(rejectEmployeeModel.getReason());

        EmployeeRejected savedEmployeeRejected = executeInTransaction(() -> {
            EmployeeRejected db = employeeRejectedDao.saveAndFlush(employeeRejected);
            userDao.updateLastActivityForCurrentUser();
            return db;
        });
        attendanceRejectedEventEventBus.publishEvent(event -> event
                .setAttendanceRejectionId(savedEmployeeRejected.getId())
                .setJobPostingApplicationId(temporaryJobPostingApplication.getId()));
    }

    @RequiresSystemUserRole
    @Override
    public void updateNoShow(UpdateNoShowModel noShowModel) {
        NoShow noShow = noShowDao.getOne(noShowModel.getId());
        noShow.setComments(noShowModel.getComments());
        noShowDao.save(noShow);
    }

    @RequiresSystemUserRole
    @Transactional
    @Override
    public void dismissNoShow(UpdateNoShowModel noShowModel) {
        NoShow noShow = noShowDao.getOne(noShowModel.getId());
        noShow.setComments(noShowModel.getComments());
        noShow.setStatus(NoShow.CLEARED);
        noShowDao.saveAndFlush(noShow);
        recalculateProNoShows(noShow.getProfessional());
    }

    protected void recalculateProNoShows(Professional dbPro) {
        long count = dbPro.getNoWorks().stream().filter(ns -> NoShow.REGISTERED.equals(ns.getStatus())).count();
        dbPro.setNoShow((int) count);
        professionalManager.save(dbPro);
    }

    @RequiresSystemUserRole
    @Transactional(readOnly = true)
    @Override
    public NoShowModel noShow(String id) {
        return commonConverter.toNoShowModel(noShowDao.getOne(id));
    }

    @RequiresSystemUserRole
    @Transactional(readOnly = true)
    @Override
    public QueryResult<NoShowModel> getNoShows(FindProfessionalNoShows queryInfo) {
        FindProfessionalNoShows.FindProfessionalNoShowsFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());
        Page<NoShowModel> professionalNoShow = professionalManager.getProfessionalNoShow(filters.getProfessionalId(), pageable);

        return queryConverter.toQueryResult(professionalNoShow, Function.identity());
    }

//    @Transactional(readOnly = true)
//    @Override
//    @PreAuthorize("@jobDayAccessAuthorizer.readAllowed(#jobDayId)")
//    public List<AttendanceAlertReplyModel> alertReplies(String jobDayId) {
//        AttendanceAlert db = attendanceAlertDao.findOne(jobDayId);
//        if (isNull(db)) {
//            return ImmutableList.of();
//        }
//
//        return db.getReply().stream().map(attendanceAlertReply -> {
//            AttendanceAlertReplyModel attendanceAlertReplyModel = new AttendanceAlertReplyModel();
//            attendanceAlertReplyModel.setTemplate(attendanceAlertReply.getTemplate());
//            attendanceAlertReplyModel.setReplyDate(attendanceAlertReply.getCreated());
//            return attendanceAlertReplyModel;
//        }).collect(Collectors.toList());
//
//    }

    @Override
    @PreAuthorize("@jobPostingApplicationAccessAuthorizer.acceptAllowed(#replyAlertAttendance.temporaryJobPostingApplicationId)")
    public void addAlertReply(ReplyAlertAttendance replyAlertAttendance) {
        Set<String> attendanceAlertIds = executeInTransaction(() -> {
            List<AttendanceAlert> attendanceAlerts = attendanceAlertDao.findCurrentAlertsByApplicationId(replyAlertAttendance.getTemporaryJobPostingApplicationId());

            if (CollectionUtils.isEmpty(attendanceAlerts)) {
                return Collections.emptySet();
            }
            AttendanceAlertReply reply = new AttendanceAlertReply();
            reply.setTemplate(replyAlertAttendance.getTemplate());
            attendanceAlertReplyDao.save(reply);

            attendanceAlerts.forEach(attendanceAlert -> {
                attendanceAlert.setReply(reply);
                attendanceAlertDao.save(attendanceAlert);
                JobDay jobDay = attendanceAlert.getJobDay();
                jobDay.setAlerted(false);
                jobDayDao.save(jobDay);
            });
            return attendanceAlerts.stream().map(AttendanceAlert::getId).collect(Collectors.toSet());
        });

        attendanceAlertIds.forEach(attendanceAlertId -> attendanceAlertRepliedEventEventBus.publishEvent(event -> event.setAttendanceAlertId(attendanceAlertId)));
    }
}
