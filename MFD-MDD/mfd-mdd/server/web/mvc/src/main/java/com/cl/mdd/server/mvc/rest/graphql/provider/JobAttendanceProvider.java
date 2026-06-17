package com.cl.mdd.server.mvc.rest.graphql.provider;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.query.FindProfessionalAttendances;
import com.cl.mdd.server.core.data.model.query.FindProfessionalAttendances.FindProfessionalAttendancesOrders;
import com.cl.mdd.server.core.data.model.query.FindProfessionalNoShows;
import com.cl.mdd.server.core.service.posting.JobAttendanceService;
import com.cl.mdd.server.mvc.rest.graphql.model.Connection;
import com.cl.mdd.server.mvc.security.WebSecurityAccess;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.cl.mdd.server.mvc.rest.graphql.model.Connection.fromQueryResult;

/**
 * Job Attendance provider
 */
@Component
public class JobAttendanceProvider implements GraphQLProvider {

    @Autowired
    private JobAttendanceService jobAttendanceService;

    @Autowired
    private WebSecurityAccess webSecurityAccess;

    @GraphQLMutation(name = "checkInAttendance")
    public void alertAttendance(@GraphQLArgument(name = "attendance") CheckInAttendance checkInAttendance) {
        jobAttendanceService.checkIn(checkInAttendance);
    }

    @GraphQLMutation(name = "alertAttendance")
    public void alertAttendance(@GraphQLArgument(name = "attendance") AlertAttendance alertAttendance) {
        jobAttendanceService.alert(alertAttendance);
    }

    @GraphQLMutation(name = "sosAttendance")
    public void sosAttendance(@GraphQLArgument(name = "attendance") RequestAttendanceSos requestAttendanceSos) {
        jobAttendanceService.requestSos(requestAttendanceSos);
    }

    @GraphQLMutation(name = "dismissSos")
    public void dismissSos(@GraphQLArgument(name = "attendance") DismissSos dismissSos) {
        jobAttendanceService.dismissSos(dismissSos);
    }

//    @GraphQLQuery(name = "attendances")
//    public List<Attendance> attendances(@GraphQLArgument(name = "date") LocalDate date) {
//        FindProfessionalAttendances query = new FindProfessionalAttendances();
//        query.getFilters().setLocalDate(date).setPracticeOwnerId(webSecurityAccess.currentUserId());
//        return jobAttendanceService.attendances(query);
//    }

    @GraphQLQuery(name = "attendances")
    public Connection<Attendance> attendances(@GraphQLArgument(name = "page") Integer page,
                                        @GraphQLArgument(name = "perPage") Integer perPage,
                                        @GraphQLArgument(name = "orders") List<FindProfessionalAttendancesOrders> orders) {
        FindProfessionalAttendances query = new FindProfessionalAttendances();

        query.getFilters().setPracticeOwnerId(webSecurityAccess.currentUserId());

        query.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);
        return fromQueryResult(jobAttendanceService.attendances(query));
    }

    @GraphQLMutation(name = "updateNoShow")
    public void updateNoShow(@GraphQLArgument(name = "updateNoShow") UpdateNoShowModel noShowModel) {
        jobAttendanceService.updateNoShow(noShowModel);
    }

    @GraphQLMutation(name = "dismissNoShow")
    public void dismissNoShow(@GraphQLArgument(name = "dismissNoShow") UpdateNoShowModel noShowModel) {
        jobAttendanceService.dismissNoShow(noShowModel);
    }

    @GraphQLQuery(name = "getNoShow")
    public NoShowModel noShow(@GraphQLArgument(name = "id") String id) {
        return jobAttendanceService.noShow(id);
    }

    @GraphQLMutation(name = "addNoShow")
    public NoShowModel addNoShow(@GraphQLArgument(name = "noShow") AddNoShowModel addNoShowModel) {
        return jobAttendanceService.addNoShow(addNoShowModel);
    }

    @GraphQLMutation(name = "replyAlertAttendance")
    public void replyAlertAttendance(@GraphQLArgument(name = "reply") ReplyAlertAttendance replyAlertAttendance) {
        jobAttendanceService.addAlertReply(replyAlertAttendance);
    }


//    @GraphQLQuery(name = "alertReplies")
//    public List<AttendanceAlertReplyModel> alertReplies(@GraphQLArgument(name = "jobDayId") String jobDayId) {
//        return jobAttendanceService.alertReplies(jobDayId);
//    }

    @GraphQLMutation(name = "rejectEmployee")
    public void rejectEmployee(@GraphQLArgument(name = "attendance") RejectEmployeeModel rejectEmployeeModel) {
        jobAttendanceService.rejectEmployee(rejectEmployeeModel);
    }

    @GraphQLQuery(name = "professionalNoShows")
    public Connection<NoShowModel> professionalNoShows(@GraphQLArgument(name = "page") Integer page,
                                                       @GraphQLArgument(name = "perPage") Integer perPage,
                                                       @GraphQLArgument(name = "professionalId") String professionalId,
                                                       @GraphQLArgument(name = "orders") List<FindProfessionalNoShows.FindProfessionalNoShowsOrders> orders) {
        FindProfessionalNoShows queryInfo = new FindProfessionalNoShows();
        queryInfo.getFilters().setProfessionalId(professionalId);
        queryInfo.getPagination()
                .setPage(page)
                .setPerPage(perPage)
                .withOrders(orders);

        return Connection.fromQueryResult(jobAttendanceService.getNoShows(queryInfo));
    }

}
