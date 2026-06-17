package com.cl.mdd.server.core.service.posting;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.FindProfessionalAttendances;
import com.cl.mdd.server.core.data.model.query.FindProfessionalNoShows;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.service.Service;

import javax.validation.Valid;

public interface JobAttendanceService extends Service {

    void checkIn(@Valid CheckInAttendance attendance);

    /**
     * The same as {@link #checkIn(CheckInAttendance)}, without security constraints, should not be exposed outside.
     */
    void internalCheckIn(CheckInAttendance attendance);

    void alert(@Valid AlertAttendance alertAttendance);

    void requestSos(@Valid RequestAttendanceSos requestAttendanceSos);

    void dismissSos(@Valid DismissSos dismissSos);

    QueryResult<Attendance> attendances(FindProfessionalAttendances query);

    NoShowModel addNoShow(@Valid AddNoShowModel noShowModel);

    void rejectEmployee(@Valid RejectEmployeeModel rejectEmployeeModel);

    void updateNoShow(UpdateNoShowModel noShowModel);

    NoShowModel noShow(String id);

    void dismissNoShow(UpdateNoShowModel noShowModel);

    QueryResult<NoShowModel> getNoShows(FindProfessionalNoShows queryInfo);

//    List<AttendanceAlertReplyModel> alertReplies(String jobDayId);

    void addAlertReply(ReplyAlertAttendance replyAlertAttendance);
}