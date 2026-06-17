package com.cl.mdd.server.mvc.rest;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.model.common.ContactModel;
import com.cl.mdd.server.core.data.model.common.FullNameModel;
import com.cl.mdd.server.core.data.model.notification.FindNotificationTemplatesQuery;
import com.cl.mdd.server.core.data.model.notification.NotificationTemplateModel;
import com.cl.mdd.server.core.data.model.query.FindSystemUsersQuery;
import com.cl.mdd.server.core.data.model.questionnaire.*;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.model.settings.SystemSettingModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest.API_PATH;
import static com.cl.mdd.server.mvc.rest.BaseMvcIntegrationTest.SECURED_API_PATH;

@Component
public class GraphQLRequestRepository {

    public static final String CONTACT_FRAGMENT =
            " contact{" +
                    " email" +
                    " fax" +
                    " phone" +
                    " address{" +
                    "   country" +
                    "   city" +
                    "   zipCode" +
                    "   street" +
                    "   state" +
                    "   latitude" +
                    "   longitude" +
                    " }," +
                    " name{" +
                    "   first" +
                    "   last" +
                    "   title" +
                    "   middle" +
                    " }" +
                    "} ";

    public static final String PRO_PROFILE_FRAGMENT =
            "  profile {" +
                    "    education" +
                    "    highestDegree" +
                    "    languages" +
                    "    skillSummary" +
                    "    workReferences {" +
                    "      name" +
                    "      phone" +
                    "      email" +
                    "    }" +
                    "    workExperiences {" +
                    "      leaveDate" +
                    "      hireDate" +
                    "      responsibilities" +
                    "      companyName" +
                    "    }" +
                    "    languages" +
                    "  }";

    private static final String COMPLETE_REGISTRATION_MUTATION = "completeRegistration(token:\\\"%s\\\")";

    private static final String PRACTICE_BY_ID =
            " practice(id:\\\"%s\\\"){" +
                    "    id" +
                    "    name" +
                    "    afterWorkPhone" +
                    "    softwares" +
                    "    webSite" +
                    "    billingAddress{" +
                    "       country" +
                    "       city" +
                    "       zipCode" +
                    "       street" +
                    "       state" +
                    "    }" +
                    "    officeManagerName" +
                    "    phone" +
                    "    secondEmail" +
                    "    status" +
                    "    practiceOwner{" +
                    "    comments" +
                    CONTACT_FRAGMENT +
                    "}" +
                    "    locations{" +
                    "       id" +
                    "       name" +
                    "       timeZone" +
                    "       rating" +
                    CONTACT_FRAGMENT +
                    "    }" +
                    " }";

    private static final String JOB_PREFERENCE_FRAGMENT = "   jobPreference {      " +
            "       willingToRelocate      " +
            "       lookingForPartTimeJob      " +
            "       desiredRatePerHour      " +
            "       lookingForFullTimeJob      " +
            "       lookingForPermanentJob      " +
            "       salaryFrom      " +
            "       eveningWorkingHoursOk      " +
            "       commutingRadius      " +
            "       availabilityDays      " +
            "       salaryTo      " +
            "       lookingForTemporaryJob      " +
            "       bayAreas    }    ";

    public static final String CURRENT_AUTHENTICATED_USER_QUERY =
            "currentAuthenticatedUserInfo{" +
                    "id " +
                    "username" +
                    " status" +
                    " name {" +
                    "  middle" +
                    "  last" +
                    "  title" +
                    "  first" +
                    "}" +
                    " roles" +
                    "}";

    public static final String CERTIFICATE_DETAILS_FIELDS_QUERY =
            "             _type_" +
                    "             id" +
                    "             certificateType {id, optional}" +
                    "             status" +
                    "             comment" +
                    "             licenseNumber" +
                    "             expirationDate" +
                    "             certificate {" +
                    "                  id" +
                    "                  name" +
                    "                  contentType" +
                    "              }";

    public static final String CERTIFICATE_DETAIL_MODEL_QUERY =
            "           ... on BaseCertificateDetailsModel {" +
                    CERTIFICATE_DETAILS_FIELDS_QUERY +
                    "           }" +
                    "           ... on OrthodonticsCertificateDetailsModel {" +
                    CERTIFICATE_DETAILS_FIELDS_QUERY +
                    "             education" +
                    "             speciality" +
                    "           }";


    public static MockHttpServletRequestBuilder addPracticeLocationRequest(AddPracticeLocation location) {
        ContactModel contact = location.getContact();
        String query =
                "  addPracticeLocation(" +
                        "    addPracticeLocation: {" +
                        "       workingHoursFrom: " + of(location.getWorkingHoursFrom()) +
                        "       workingHoursTo: " + of(location.getWorkingHoursTo()) +
                        "       timeZone: " + of(location.getTimeZone()) +
                        "       name: " + of(location.getName()) +
                        "       contact:" + ofContact(contact) +
                        "    }" +
                        "  )" +
                        "  {" +
                        "     id" +
                        "     name" +
                        "     workingHoursFrom" +
                        "     workingHoursTo" +
                        "     timeZone" +
                        CONTACT_FRAGMENT +
                        "  }";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder publishSimpleTemporaryJobPosting(PublishSimpleTemporaryJobPosting publishSimpleTemporaryJobPosting) {
        String query =
                "  publishSimpleTemporary(" +
                        "    jobPosting: {" +
                        "       name: " + of(publishSimpleTemporaryJobPosting.getName()) +
                        "       comment: " + of(publishSimpleTemporaryJobPosting.getComment()) +
                        "       practiceLocationId: " + of(publishSimpleTemporaryJobPosting.getPracticeLocationId()) +
                        "       startDate: " + of(publishSimpleTemporaryJobPosting.getStartDate()) +
                        "       endDate: " + of(publishSimpleTemporaryJobPosting.getEndDate()) +
                        "       startTime: " + of(publishSimpleTemporaryJobPosting.getStartTime()) +
                        "       endTime: " + of(publishSimpleTemporaryJobPosting.getEndTime()) +
                        "       requiredLanguages: " + of(publishSimpleTemporaryJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(publishSimpleTemporaryJobPosting.getRequiredSubcategories()) +
                        "       preferredCandidateId: " + of(publishSimpleTemporaryJobPosting.getPreferredCandidateId()) +
                        "    }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder applyForTemporaryJob(ApplicationForTemporaryJob applicationForTemporaryJob) {
        String query =
                "  apply(" +
                        "    temporaryJobApplication: {" +
                        "       jobPostingId: " + of(applicationForTemporaryJob.getJobPostingId()) +
                        "       workingDays: " + of(applicationForTemporaryJob.getWorkingDays()) +
                        "    }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder applyForPermanentJob(ApplicationForPermanentJob applicationForPermanentJob) {
        String query =
                "  apply(" +
                        "    permanentJobApplication: {" +
                        "       jobPostingId: " + of(applicationForPermanentJob.getJobPostingId()) +
                        "    }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder scheduleInterview(ScheduleJobInterview scheduleJobInterview) {
        String query =
                "  scheduleInterview(" +
                        "    interview: {" +
                        "       applicationId: " + of(scheduleJobInterview.getApplicationId()) +
                        "       working: " + of(scheduleJobInterview.isWorking()) +
                        "       comments: " + of(scheduleJobInterview.getComments()) +
                        "       options: " + ofOptions(scheduleJobInterview.getOptions()) +
                        "    }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder cancelInterview(String id) {
        String query =
                "  cancelInterview(" +
                        "    id: " + of(id) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder rejectInterview(String id) {
        String query =
                "  rejectInterview(" +
                        "    id: " + of(id) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder acceptInterview(String optionId) {
        String query =
                "  acceptInterview(" +
                        "    optionId: " + of(optionId) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder interview(String id) {
        String query =
                "  interview(" +
                        "    id: " + of(id) +
                        "  ){" +
                        "id " +
                        "working " +
                        "comments " +
                        "options {" +
                        "       id " +
                        "       dateTime " +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder withdrawApplication(String applicationId) {
        String query =
                "  withdrawApplication(" +
                        "   applicationId: " + of(applicationId) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder bookApplication(String applicationId) {
        String query =
                "  bookApplication(" +
                        "   applicationId: " + of(applicationId) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder cancelApplication(String applicationId) {
        String query =
                "  cancelApplication(" +
                        "   applicationId: " + of(applicationId) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }


    public static MockHttpServletRequestBuilder acceptApplication(String applicationId) {
        String query =
                "  acceptApplication(" +
                        "   applicationId: " + of(applicationId) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }


    public static MockHttpServletRequestBuilder rejectApplication(String applicationId) {
        String query =
                "  rejectApplication(" +
                        "   applicationId: " + of(applicationId) +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder rejectEmployee(RejectEmployeeModel rejectEmployeeModel) {
        String query =
                "  rejectEmployee(" +
                        "   attendance: {" +
                        "               jobDayId : " + of(rejectEmployeeModel.getJobDayId()) +
                        "               reason : " + of(rejectEmployeeModel.getReason()) +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder fetchAttendances(String order) {
        String query =
                "  attendances( page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]) " +
                        "{" +
                        "       count" +
                        "       nodes{" +
                        "       jobDayId" +
                        "       attendanceStartDateTime" +
                        "       attendanceEndDateTime" +
                        "       date" +
                        "       startTime" +
                        "       endTime" +
                        "       professionalId" +
                        "       professionalFirstName" +
                        "       professionalLastName" +
                        "       jobPostingName" +
                        "       jobDayStatus" +
                        "       practiceLocationName" +
                        "}" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder alertAttendance(AlertAttendance alertAttendance) {
        String query =
                "  alertAttendance(" +
                        "   attendance: {" +
                        "               jobDayId : " + of(alertAttendance.getJobDayId()) +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder checkInAttendance(CheckInAttendance checkInAttendance) {
        String query =
                "  checkInAttendance(" +
                        "   attendance: {" +
                        "               jobDayId : " + of(checkInAttendance.getJobDayId()) +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder locationReview(ProfessionalToLocationReview review) {
        String query =
                "  createLocationReview(" +
                        "   review: {" +
                        "               applicationId : " + of(review.getApplicationId()) +
                        "               comment : " + of(review.getComment()) +
                        "               wouldWorkPermanently : " + review.isWouldWorkPermanently() +
                        "               rate : " + review.getRate() +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalReview(LocationToProfessionalReview review) {
        String query =
                "  createProfessionalReview(" +
                        "   review: {" +
                        "               applicationId : " + of(review.getApplicationId()) +
                        "               comment : " + of(review.getComment()) +
                        "               wouldHire : " + review.isWouldHire() +
                        "               appearanceRate : " + review.getAppearanceRate() +
                        "               punctualityRate : " + review.getPunctualityRate() +
                        "               workQualityRate : " + review.getWorkQualityRate() +
                        "               communicationRate : " + review.getCommunicationRate() +
                        "               professionalismRate : " + review.getProfessionalismRate() +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder locationReview(String id) {
        String query =
                "  locationReview( id:" + of(id) +
                        " ) {" +
                        "               applicationId" +
                        "               comment" +
                        "               wouldWorkPermanently" +
                        "               rate" +
                        "   }";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalReview(String id) {
        String query =
                "  professionalReview( id :" + of(id) +
                        ")   {" +
                        "               applicationId " +
                        "               comment " +
                        "               wouldHire " +
                        "               appearanceRate " +
                        "               punctualityRate " +
                        "               workQualityRate " +
                        "               communicationRate " +
                        "               professionalismRate " +
                        "   }";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder deleteLocationReview(String id) {
        String query =
                "  deleteLocationReview( id:" + of(id) +
                        " )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder deleteProfessionalReview(String id) {
        String query =
                "  deleteProfessionalReview( id :" + of(id) +
                        ")";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updateLocationReview(ProfessionalToLocationReview review) {
        String query =
                "  updateLocationReview(" +
                        "   review: {" +
                        "               applicationId : " + of(review.getApplicationId()) +
                        "               comment : " + of(review.getComment()) +
                        "               wouldWorkPermanently : " + review.isWouldWorkPermanently() +
                        "               rate : " + review.getRate() +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updateProfessionalReview(LocationToProfessionalReview review) {
        String query =
                "  updateProfessionalReview(" +
                        "   review: {" +
                        "               applicationId : " + of(review.getApplicationId()) +
                        "               comment : " + of(review.getComment()) +
                        "               wouldHire : " + review.isWouldHire() +
                        "               appearanceRate : " + review.getAppearanceRate() +
                        "               punctualityRate : " + review.getPunctualityRate() +
                        "               workQualityRate : " + review.getWorkQualityRate() +
                        "               communicationRate : " + review.getCommunicationRate() +
                        "               professionalismRate : " + review.getProfessionalismRate() +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalReviews(String order, String professionalId) {
        String query =
                "  professionalReviews(" +
                        "       professionalId: " + of(professionalId) +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           jobPostingName" +
                        "           practiceOwnerFirstName" +
                        "           practiceOwnerLastName" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           endDate" +
                        "           professionalismRate" +
                        "           communicationRate" +
                        "           workQualityRate" +
                        "           punctualityRate" +
                        "           appearanceRate" +
                        "           wouldHire" +
                        "           blackListed" +
                        "           feedbackDate" +
                        "           comment" +
                        "           totalScore" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }


    public static MockHttpServletRequestBuilder previouslyHiredProfessionals(String order) {
        String query =
                "  previouslyHiredProfessionals(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           firstName" +
                        "           lastName" +
                        "           lastEmploymentDate" +
                        "           blackListed" +
                        "           totalRating" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder directBookingCandidates(String order, String candidateName, String requiredSubcategory, String practiceLocationId) {
        String query =
                "  directBookingCandidates(" +
                        "       candidateName: " + of(candidateName) +
                        "       requiredSubcategories: [" + of(requiredSubcategory) + "]" +
                        "       practiceLocationId: " + of(practiceLocationId) +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           firstName" +
                        "           lastName" +
                        "           ratePerHour" +
                        "           totalRating" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalPreviousJobsForEmployer(String order, String professionalId) {
        String query =
                "  professionalPreviousJobsForEmployer(" +
                        "       professionalId:" + of(professionalId) +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           jobPostingApplicationId" +
                        "           jobPostingName" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           endDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalPreviousJobsForEmployee(String order, LocalDate startDate) {
        String query =
                "  professionalPreviousJobsForEmployee(" +
                        "       startDate: " + of(startDate) +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           jobPostingApplicationId" +
                        "           jobPostingId" +
                        "           jobPostingName" +
                        "           startDate" +
                        "           endDate" +
                        "           hasReview" +
                        "           practiceName" +
                        "           practiceLocationName" +
                        "           distance" +
                        "           locationRating" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }


    public static MockHttpServletRequestBuilder locationReviews(String order, String practiceId) {
        String query =
                "  locationReviews(" +
                        "       practiceId: " + of(practiceId) +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "               id" +
                        "               jobPostingName" +
                        "               professionalFirstName" +
                        "               professionalLastName" +
                        "               practiceLocationName" +
                        "               startDate" +
                        "               endDate" +
                        "               rate" +
                        "               wouldWorkPermanently" +
                        "               blackListed" +
                        "               comment" +
                        "               feedbackDate" +
                        "       }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder replyAlertAttendance(ReplyAlertAttendance replyAlertAttendance) {
        String query =
                "  replyAlertAttendance(" +
                        "   reply: {" +
                        "               temporaryJobPostingApplicationId : " + of(replyAlertAttendance.getTemporaryJobPostingApplicationId()) +
                        "               template : " + of(replyAlertAttendance.getTemplate()) +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder alertReplies(String jobDayId) {
        String query =
                "  alertReplies(" +
                        "   jobDayId: " + of(jobDayId) +
                        "  ){" +
                        "   template" +
                        "   replyDate" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder sosAttendance(RequestAttendanceSos requestAttendanceSos) {
        String query =
                "  sosAttendance(" +
                        "   attendance: {" +
                        "               jobDayId : " + of(requestAttendanceSos.getJobDayId()) +
                        "               noShow : " + of(requestAttendanceSos.isNoShow()) +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder dismissSos(DismissSos dismissSos) {
        String query =
                "  dismissSos(" +
                        "   attendance: {" +
                        "               jobPostingId : " + of(dismissSos.getJobPostingId()) +
                        "   }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder publishSimplePermanentJobPosting(PublishSimplePermanentJobPosting publishSimplePermanentJobPosting) {
        String query =
                "  publishSimplePermanent(" +
                        "    jobPosting: {" +
                        "       name: " + of(publishSimplePermanentJobPosting.getName()) +
                        "       comment: " + of(publishSimplePermanentJobPosting.getComment()) +
                        "       practiceLocationId: " + of(publishSimplePermanentJobPosting.getPracticeLocationId()) +
                        "       preferredCandidateId: " + of(publishSimplePermanentJobPosting.getPreferredCandidateId()) +
                        "       startDate: " + of(publishSimplePermanentJobPosting.getStartDate()) +
                        "       requiredLanguages: " + of(publishSimplePermanentJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(publishSimplePermanentJobPosting.getRequiredSubcategories()) +
                        "       workSchedules: " + ofWorkSchedules(publishSimplePermanentJobPosting.getWorkSchedules()) +
                        "    }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalTemporaryJobPostings(String order, LocalDate startDate, LocalDate endDate, String status) {
        String query =
                "  professionalTemporaryJobPostings(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       status: " + of(status) +
                        "       startDate: " + of(startDate) +
                        "       endDate: " + of(endDate) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           applicationId" +
                        "           name" +
                        "           applicationStatus" +
                        "           practiceName" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           endDate" +
                        "           startTime" +
                        "           endTime" +
                        "           distance" +
                        "           postedDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder blackListedProfessionalDetails(String order, String professionalId) {
        String query =
                "  blackListedProfessionalDetails(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       professionalId: " + of(professionalId) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           practiceId" +
                        "           practiceName" +
                        "           practiceOwnerFirstName" +
                        "           practiceOwnerLastName" +
                        "           professionalFirstName" +
                        "           professionalLastName" +
                        "           blackListDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder blackListedLocationDetails(String order, String professionalId) {
        String query =
                "  blackListedLocationDetails(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       professionalId: " + of(professionalId) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           practiceId" +
                        "           locationId" +
                        "           practiceName" +
                        "           practiceLocationName" +
                        "           practiceOwnerFirstName" +
                        "           practiceOwnerLastName" +
                        "           professionalFirstName" +
                        "           professionalLastName" +
                        "           blackListDate" +
                        "           unblackListDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }


    public static MockHttpServletRequestBuilder blackListedLocationSummary(String order) {
        String query =
                "  blackListedLocationSummary(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           practiceId" +
                        "           locationId" +
                        "           practiceName" +
                        "           practiceLocationId" +
                        "           practiceLocationName" +
                        "           professionalFirstName" +
                        "           professionalLastName" +
                        "           blackListDate" +
                        "           unblackListDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalPermanentJobPostings(String order, LocalDate startDate, String status) {
        String query =
                "  professionalPermanentJobPostings(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       status: " + of(status) +
                        "       startDate: " + of(startDate) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           applicationId" +
                        "           name" +
                        "           applicationStatus" +
                        "           practiceName" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           startTime" +
                        "           distance" +
                        "           postedDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder practiceOwnerTemporaryJobPostings(final String order, LocalDate startDate, LocalDate endDate, String status) {
        String query =
                "  practiceOwnerTemporaryJobPostings(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       status: " + of(status) +
                        "       startDate: " + of(startDate) +
                        "       endDate: " + of(endDate) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           name" +
                        "           status" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           endDate" +
                        "           startTime" +
                        "           endTime" +
                        "           applicants" +
                        "           postedDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder practiceOwnerPermanentJobPostings(String order, LocalDate startDate, String status) {
        String query =
                "  practiceOwnerPermanentJobPostings(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       status: " + of(status) +
                        "       startDate: " + of(startDate) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           name" +
                        "           status" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           startTime" +
                        "           applicants" +
                        "           postedDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }


    public static MockHttpServletRequestBuilder systemUserTemporaryJobPostings(String order, LocalDate startDate, LocalDate endDate, String status, List<String> specialties, Double distance, Double lat, Double lng) {
        String query =
                "  systemUserTemporaryJobPostings(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       distance: " + distance +
                        "       lat: " + lat +
                        "       lng: " + lng +
                        "       status: " + of(status) +
                        "       startDate: " + of(startDate) +
                        "       endDate: " + of(endDate) +
                        "       specialties: " + of(specialties) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           name" +
                        "           status" +
                        "           practiceOwnerFirstName" +
                        "           practiceOwnerLastName" +
                        "           practiceName" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           endDate" +
                        "           startTime" +
                        "           endTime" +
                        "           applicants" +
                        "           postedDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder systemUserPermanentJobPostings(String order, LocalDate startDate, String status, List<String> specialities, Double distance, Double lat, Double lng) {
        String query =
                "  systemUserPermanentJobPostings(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       distance: " + distance +
                        "       lat: " + lat +
                        "       lng: " + lng +
                        "       status: " + of(status) +
                        "       startDate: " + of(startDate) +
                        "       specialties: " + of(specialities) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           name" +
                        "           status" +
                        "           practiceOwnerFirstName" +
                        "           practiceOwnerLastName" +
                        "           practiceName" +
                        "           practiceLocationName" +
                        "           startDate" +
                        "           startTime" +
                        "           applicants" +
                        "           postedDate" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder interviews(String order, LocalDate date, String status, String practiceOwnerId) {
        String query =
                "  interviews(" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       status: " + of(status) +
                        "       date: " + of(date) +
                        "       practiceOwnerId: " + of(practiceOwnerId) +
                        "       orders: [" + order + "]" +
                        "  ) {" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           jobPostingName" +
                        "           practiceOwnerFirstName" +
                        "           practiceOwnerLastName" +
                        "           practiceName" +
                        "           practiceLocationName" +
                        "           professionalFirstName" +
                        "           professionalLastName" +
                        "           status" +
                        "           date" +
                        "           time" +
                        "           type" +
                        "           numberOfInterview" +
                        "   }" +
                        "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updateToSimplePermanentJobPosting(SimplePermanentJobPosting simplePermanentJobPosting) {
        String query =
                "  updateToSimplePermanent(" +
                        "    jobPosting: {" +
                        "       id: " + of(simplePermanentJobPosting.getId()) +
                        "       name: " + of(simplePermanentJobPosting.getName()) +
                        "       comment: " + of(simplePermanentJobPosting.getComment()) +
                        "       practiceLocationId: " + of(simplePermanentJobPosting.getPracticeLocationId()) +
                        "       startDate: " + of(simplePermanentJobPosting.getStartDate()) +
                        "       requiredLanguages: " + of(simplePermanentJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(simplePermanentJobPosting.getRequiredSubcategories()) +
                        "       workSchedules: " + ofWorkSchedules(simplePermanentJobPosting.getWorkSchedules()) +
                        "    }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updateToSimpleTemporaryJobPosting(SimpleTemporaryJobPosting simpleTemporaryJobPosting) {
        String query =
                "  updateToSimpleTemporary(" +
                        "    jobPosting: {" +
                        "       id: " + of(simpleTemporaryJobPosting.getId()) +
                        "       name: " + of(simpleTemporaryJobPosting.getName()) +
                        "       comment: " + of(simpleTemporaryJobPosting.getComment()) +
                        "       practiceLocationId: " + of(simpleTemporaryJobPosting.getPracticeLocationId()) +
                        "       startDate: " + of(simpleTemporaryJobPosting.getStartDate()) +
                        "       endDate: " + of(simpleTemporaryJobPosting.getEndDate()) +
                        "       startTime: " + of(simpleTemporaryJobPosting.getStartTime()) +
                        "       endTime: " + of(simpleTemporaryJobPosting.getEndTime()) +
                        "       requiredLanguages: " + of(simpleTemporaryJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(simpleTemporaryJobPosting.getRequiredSubcategories()) +
                        "       preferredCandidateId: " + of(simpleTemporaryJobPosting.getPreferredCandidateId()) +
                        "    }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder jobPosting(String id) {
        String query =
                "  jobPosting(" +
                        "    id : " + of(id) +
                        " )   {" +
                        "       ...on ViewComplexTemporaryJobPosting{" +
                        "           id" +
                        "           name" +
                        "           comment" +
                        "           practiceLocationId" +
                        "           startDate" +
                        "           endDate" +
                        "           requiredLanguages" +
                        "           requiredSubcategories" +
                        "           preferredCandidateId" +
                        "           jobDays {" +
                        "                       date" +
                        "                       startTime" +
                        "                       endTime" +
                        "                       excluded" +
                        "                   }" +
                        "           zonedJobDays {" +
                        "                       date" +
                        "                       startTime" +
                        "                       endTime" +
                        "                       excluded" +
                        "                   }" +
                        "       }" +
                        "       ...on ViewSimpleTemporaryJobPosting{" +
                        "           id" +
                        "           name" +
                        "           comment" +
                        "           practiceLocationId" +
                        "           startDate" +
                        "           startTime" +
                        "           endDate" +
                        "           endTime" +
                        "           requiredLanguages" +
                        "           requiredSubcategories" +
                        "           preferredCandidateId" +
                        "           zonedJobDays {" +
                        "                       date" +
                        "                       startTime" +
                        "                       endTime" +
                        "                       excluded" +
                        "                   }" +
                        "       }" +
                        "       ...on ViewWeeklyTemporaryJobPosting{" +
                        "           id" +
                        "           name" +
                        "           comment" +
                        "           practiceLocationId" +
                        "           startDate" +
                        "           endDate" +
                        "           requiredLanguages" +
                        "           requiredSubcategories" +
                        "           preferredCandidateId" +
                        "           workSchedules {" +
                        "               weekDay" +
                        "               startTime" +
                        "               endTime" +
                        "           }" +
                        "           zonedJobDays {" +
                        "                       date" +
                        "                       startTime" +
                        "                       endTime" +
                        "                       excluded" +
                        "                   }" +
                        "       }" +
                        "       ...on SimplePermanentJobPosting{" +
                        "           id" +
                        "           name" +
                        "           comment" +
                        "           practiceLocationId" +
                        "           startDate" +
                        "           requiredLanguages" +
                        "           requiredSubcategories" +
                        "           workSchedules {" +
                        "               weekDay" +
                        "               startTime" +
                        "               endTime" +
                        "           }" +
                        "       }" +
                        "    }";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder publishWeeklyTemporaryJobPosting(PublishWeeklyTemporaryJobPosting publishWeeklyTemporaryJobPosting) {
        String query =
                "  publishWeeklyTemporary(" +
                        "    jobPosting: {" +
                        "       name: " + of(publishWeeklyTemporaryJobPosting.getName()) +
                        "       comment: " + of(publishWeeklyTemporaryJobPosting.getComment()) +
                        "       practiceLocationId: " + of(publishWeeklyTemporaryJobPosting.getPracticeLocationId()) +
                        "       startDate: " + of(publishWeeklyTemporaryJobPosting.getStartDate()) +
                        "       endDate: " + of(publishWeeklyTemporaryJobPosting.getEndDate()) +
                        "       requiredLanguages: " + of(publishWeeklyTemporaryJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(publishWeeklyTemporaryJobPosting.getRequiredSubcategories()) +
                        "       preferredCandidateId: " + of(publishWeeklyTemporaryJobPosting.getPreferredCandidateId()) +
                        "       workSchedules: " + ofWorkSchedules(publishWeeklyTemporaryJobPosting.getWorkSchedules()) +
                        "       }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updateToWeeklyTemporaryJobPosting(WeeklyTemporaryJobPosting weeklyTemporaryJobPosting) {
        String query =
                "  updateToWeeklyTemporary(" +
                        "    jobPosting: {" +
                        "       id: " + of(weeklyTemporaryJobPosting.getId()) +
                        "       name: " + of(weeklyTemporaryJobPosting.getName()) +
                        "       comment: " + of(weeklyTemporaryJobPosting.getComment()) +
                        "       practiceLocationId: " + of(weeklyTemporaryJobPosting.getPracticeLocationId()) +
                        "       startDate: " + of(weeklyTemporaryJobPosting.getStartDate()) +
                        "       endDate: " + of(weeklyTemporaryJobPosting.getEndDate()) +
                        "       requiredLanguages: " + of(weeklyTemporaryJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(weeklyTemporaryJobPosting.getRequiredSubcategories()) +
                        "       preferredCandidateId: " + of(weeklyTemporaryJobPosting.getPreferredCandidateId()) +
                        "       workSchedules: " + ofWorkSchedules(weeklyTemporaryJobPosting.getWorkSchedules()) +
                        "       }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder publishComplexTemporaryJobPosting(PublishComplexTemporaryJobPosting publishComplexTemporaryJobPosting) {
        String query =
                "  publishComplexTemporary(" +
                        "    jobPosting: {" +
                        "       name: " + of(publishComplexTemporaryJobPosting.getName()) +
                        "       comment: " + of(publishComplexTemporaryJobPosting.getComment()) +
                        "       practiceLocationId: " + of(publishComplexTemporaryJobPosting.getPracticeLocationId()) +
                        "       startDate: " + of(publishComplexTemporaryJobPosting.getStartDate()) +
                        "       endDate: " + of(publishComplexTemporaryJobPosting.getEndDate()) +
                        "       requiredLanguages: " + of(publishComplexTemporaryJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(publishComplexTemporaryJobPosting.getRequiredSubcategories()) +
                        "       preferredCandidateId: " + of(publishComplexTemporaryJobPosting.getPreferredCandidateId()) +
                        "       jobDays: " + ofJobDays(publishComplexTemporaryJobPosting.getJobDays()) +
                        "           " +
                        "       }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updateToComplexTemporaryJobPosting(ComplexTemporaryJobPosting complexTemporaryJobPosting) {
        String query =
                "  updateToComplexTemporary(" +
                        "    jobPosting: {" +
                        "       id: " + of(complexTemporaryJobPosting.getId()) +
                        "       name: " + of(complexTemporaryJobPosting.getName()) +
                        "       comment: " + of(complexTemporaryJobPosting.getComment()) +
                        "       practiceLocationId: " + of(complexTemporaryJobPosting.getPracticeLocationId()) +
                        "       startDate: " + of(complexTemporaryJobPosting.getStartDate()) +
                        "       endDate: " + of(complexTemporaryJobPosting.getEndDate()) +
                        "       requiredLanguages: " + of(complexTemporaryJobPosting.getRequiredLanguages()) +
                        "       requiredSubcategories: " + of(complexTemporaryJobPosting.getRequiredSubcategories()) +
                        "       preferredCandidateId: " + of(complexTemporaryJobPosting.getPreferredCandidateId()) +
                        "       jobDays: " + ofJobDays(complexTemporaryJobPosting.getJobDays()) +
                        "           " +
                        "       }" +
                        "  )";

        return securedMutationRequestBuilder(query);
    }

    private static String ofJobDays(List<JobDayModel> jobDays) {
        StringBuilder of = new StringBuilder();
        of.append("[");

        jobDays.forEach(jobDayModel -> {
            of.append(" { ");
            of.append("  date : ").append(of(jobDayModel.getDate()));
            of.append("  startTime : ").append(of(jobDayModel.getStartTime()));
            of.append("  endTime : ").append(of(jobDayModel.getEndTime()));
            of.append("  excluded : ").append(of(jobDayModel.isExcluded()));
            of.append(" }, ");
        });

        of.append("]");
        return of.toString();
    }

    private static String ofWorkSchedules(List<WorkScheduleModel> workScheduleModels) {
        StringBuilder of = new StringBuilder();
        of.append("[");

        workScheduleModels.forEach(workScheduleModel -> {
            of.append(" { ");
            of.append("  weekDay : ").append(of(workScheduleModel.getWeekDay()));
            of.append("  startTime : ").append(of(workScheduleModel.getStartTime()));
            of.append("  endTime : ").append(of(workScheduleModel.getEndTime()));
            of.append(" }, ");
        });

        of.append("]");
        return of.toString();
    }


    private static String ofOptions(List<JobInterviewScheduleOption> options) {
        StringBuilder of = new StringBuilder();
        of.append("[");

        options.forEach(option -> {
            of.append(" { ");
            of.append("  date : ").append(of(option.getDate()));
            of.append("  time : ").append(of(option.getTime()));
            of.append(" }, ");
        });

        of.append("]");
        return of.toString();
    }

    public static MockHttpServletRequestBuilder updatePracticeLocationRequest(AddPracticeLocation location) {
        ContactModel contact = location.getContact();
        String query =
                "  updatePracticeLocation(" +
                        "    updatePracticeLocation: {" +
                        "       name: " + of(location.getName()) +
                        "       contact:" + ofContact(contact) +
                        "    }" +
                        "  )" +
                        "  {" +
                        "     id" +
                        "     name" +
                        CONTACT_FRAGMENT +
                        "  }";

        return securedMutationRequestBuilder(query);
    }

    private static String ofContact(ContactModel contact) {
        if (contact != null) {
            return "{" +
                    "   email: " + of(contact.getEmail()) +
                    "   fax: " + of(contact.getFax()) +
                    "   phone: " + of(contact.getPhone()) +
                    "   address: " + ofAddress(contact.getAddress()) +
                    "   name: " + ofFullName(contact.getName()) +
                    "}";
        }
        return null;
    }

    private static String ofAddress(AddressModel address) {
        if (address != null) {
            return "{" +
                    "   country: " + of(address.getCountry()) +
                    "   city: " + of(address.getCity()) +
                    "   zipCode: " + of(address.getZipCode()) +
                    "   street: " + of(address.getStreet()) +
                    "   state: " + of(address.getState()) +
                    "   latitude: " + address.getLatitude() +
                    "   longitude: " + address.getLongitude() +
                    "}";
        }
        return null;
    }

    private static String ofFullName(FullNameModel name) {
        if (name != null) {
            return "{" +
                    "   first: " + of(name.getFirst()) +
                    "   last: " + of(name.getLast()) +
                    "   title: " + of(name.getTitle()) +
                    "   middle: " + of(name.getMiddle()) +
                    "}";
        }
        return null;
    }

    public static MockHttpServletRequestBuilder addProfessionalSubCategoriesRequest(Set<String> subCategories) {
        String query = "  addProfessionalSubcategories(" +
                "      subcategories: " + of(subCategories) +
                "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder listSubcategoriesRequest() {
        return securedQueryRequestBuilder(" subcategories {id, name, category {id, name}, certificateTypes {id, optional} }");
    }

    public static MockHttpServletRequestBuilder listProfessionalSubCategories(String order) {
        String query =
                "  professionalSubcategories (" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           subCategoryName" +
                        "           categoryName" +
                        "           status" +
                        "       }" +
                        "}";
        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalSubcategoriesByProfessionalId(String id, String order) {
        String query =
                "  professionalSubcategoriesByProfessionalId (" +
                        "       professionalId: " + of(id) +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           id" +
                        "           subCategoryName" +
                        "           categoryName" +
                        "           status" +
                        "       }" +
                        "}";
        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder listProfessionalRequiredCertificates(String order) {
        String query =
                "  professionalRequiredCertificates (" +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           type" +
                        "           status" +
                        "           certificateId" +
                        "           optional" +
                        "       }" +
                        "}";
        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder professionalRequiredCertificatesByProfessionalId(String id, String order) {
        String query =
                "  professionalRequiredCertificatesByProfessionalId (" +
                        "       professionalId: " + of(id) +
                        "       page: 0" +
                        "       perPage: 50" +
                        "       orders: [" + order + "]" +
                        "  ){" +
                        "       count" +
                        "       nodes{" +
                        "           type" +
                        "           status" +
                        "           certificateId" +
                        "           optional" +
                        "       }" +
                        "}";
        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder certificateDetails(String id) {
        String query = "  certificateDetails (id: " + of(id) + ")" +
                " { " + CERTIFICATE_DETAIL_MODEL_QUERY + " } ";
        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder approveCertificate(String id) {
        String query = " approveCertificateDetails (id: " + of(id) + ") ";
        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder rejectCertificate(RejectCertificateDetailsModel model) {
        String query = " rejectCertificateDetails (" +
                "reject: " +
                " { " +
                "   id: " + of(model.getId()) +
                "   comment: " + of(model.getComment()) +
                " } " +
                ") ";
        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder listProfessionalCertificates() {
        String query = "  proSubcategories (" +
                "    page: 0" +
                "    perPage: 10" +
                "  ) {" +
                "    nodes {" +
                "      certificateDetails {" +
                "             certificateDetails {" +
                "                  id" +
                "                  name" +
                "                  contentType" +
                "              }" +
                "       }" +
                "    }" +
                "  }  ";
        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder deleteProfessionalSubCategory(String subCategoryId) {
        String query = "  deleteProfessionalSubcategory(" +
                "      id: " + of(subCategoryId) +
                "  )";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updatePracticeLocationRequest(UpdatePracticeLocation location) {
        ContactModel contact = location.getContact();
        String query =
                "  updatePracticeLocation(" +
                        "    updatePracticeLocation: {" +
                        "       id: " + of(location.getId()) +
                        "       name: " + of(location.getName()) +
                        "       workingHoursFrom: " + of(location.getWorkingHoursFrom()) +
                        "       workingHoursTo: " + of(location.getWorkingHoursTo()) +
                        "       timeZone: " + of(location.getTimeZone()) +
                        "       contact:" + ofContact(contact) +
                        "    }" +
                        "  )" +
                        "  {" +
                        "     id" +
                        "     name" +
                        "     workingHoursFrom" +
                        "     workingHoursTo" +
                        "     timeZone" +
                        CONTACT_FRAGMENT +
                        "  }";

        return securedMutationRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder practiceByIdRequest(String id) {
        Validate.notBlank(id, "invalid id");
        return securedQueryRequestBuilder(String.format(PRACTICE_BY_ID, id));
    }

    public static MockHttpServletRequestBuilder deletePracticeLocationRequest(String id) {
        Validate.notBlank(id, "invalid id");
        String mutation = "deletePracticeLocation(id: " + of(id) + ")";
        return securedMutationRequestBuilder(mutation);
    }

    public static MockHttpServletRequestBuilder deleteJobPosting(String id) {
        Validate.notBlank(id, "invalid id");
        String mutation = "deleteJobPosting(id: " + of(id) + ")";
        return securedMutationRequestBuilder(mutation);
    }

    public static MockHttpServletRequestBuilder cancelJobPosting(String id) {
        Validate.notBlank(id, "invalid id");
        String mutation = "cancelJobPosting(id: " + of(id) + ")";
        return securedMutationRequestBuilder(mutation);
    }

    public static MockHttpServletRequestBuilder professional(String id) {
        return securedQueryRequestBuilder("professional ( id : " + of(id) +
                "   )" +
                "   {    " +
                "   id  " +
                "   notificationsEnabled  " +
                "   comments  " +
                "   status  " +
                "   rating  " +
                "       " + JOB_PREFERENCE_FRAGMENT + " " +
                "       " + CONTACT_FRAGMENT + " " +
                "       " + PRO_PROFILE_FRAGMENT + " " +
                "  }");
    }

    public static MockHttpServletRequestBuilder currentAuthenticatedUserRequest() {
        return securedQueryRequestBuilder(CURRENT_AUTHENTICATED_USER_QUERY);
    }

    public static RequestBuilder completeRegistrationRequest(String token) {
        Validate.notBlank(token, "Invalid token");
        return publicMutationRequestBuilder(String.format(COMPLETE_REGISTRATION_MUTATION, token));
    }

    public static MockHttpServletRequestBuilder createSystemUserRequest(RegisterSystemUser registerSystemUser) {
        String registerSystemUserMutation =
                "registerSystemUser(" +
                        "   systemUser: {" +
                        "       username: " + of(registerSystemUser.getUsername()) +
                        "       password: " + of(registerSystemUser.getPassword()) +
                        "       contact: " + ofContact(registerSystemUser.getContact()) +
                        "   }" +
                        ") {" +
                        "   id" +
                        "   modified" +
                        "   state" +
                        "   contact{" +
                        "       name{" +
                        "           first" +
                        "           last" +
                        "       }" +
                        "   }" +
                        "}";

        return securedMutationRequestBuilder(registerSystemUserMutation);
    }

    public static MockHttpServletRequestBuilder completeRegistrationSystemUserRequest(String token, String password) {
        Validate.notBlank(token);
        String completeRegistrationMutation = "completeRegistrationSystemUser(" +
                "   token:" + of(token) +
                "   password:" + of(password) +
                ")";

        return publicMutationRequestBuilder(completeRegistrationMutation);
    }

    public static MockHttpServletRequestBuilder getSystemUserRequest(String id) {
        String query = "systemUser(" +
                "   id:" + of(id) +
                "){" +
                "   id" +
                "   modified" +
                "   state" +
                "   contact{" +
                "       phone" +
                "       name{" +
                "           first" +
                "           last" +
                "       }" +
                "   }" +
                "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder querySystemUsersRequest(Integer page,
                                                                        Integer perPage,
                                                                        List<FindSystemUsersQuery.SystemUsersOrder> orders) {
        String query = "systemUsers(" +
                "   page:" + page +
                "   perPage:" + perPage +
                "   orders:" + orders +
                "){" +
                "   count" +
                "   nodes{" +
                "       id" +
                "       modified" +
                "       state" +
                "       contact{" +
                "           name{" +
                "               first" +
                "               last" +
                "           }" +
                "       }" +
                "   }" +
                "}";

        return securedQueryRequestBuilder(query);
    }

    public static MockHttpServletRequestBuilder updateSystemUserRequest(String id,
                                                                        ContactModel contactModel) {
        String updateSystemUserMutation = "   updateSystemUser(" +
                "       id: " + of(id) +
                "       contact: " + ofContact(contactModel) +
                "   )";

        return securedMutationRequestBuilder(updateSystemUserMutation);
    }

    public static MockHttpServletRequestBuilder activateDeactivateSystemUserRequest(String id,
                                                                                    boolean enabled) {
        String activateDeactivateMutation = "   activateDeactivateSystemUser(" +
                "       id: " + of(id) +
                "       enabled: " + of(enabled) +
                "   ) {" +
                "       id" +
                "       status" +
                "   }";

        return securedMutationRequestBuilder(activateDeactivateMutation);
    }

    public static RequestBuilder createProfessionalRequest(RegisterProfessional registerProfessional) {
        String registerProfessionalMutation = "   registerProfessional(" +
                "       professional: {" +
                "           username: " + of(registerProfessional.getUsername()) +
                "           password: " + of(registerProfessional.getPassword()) +
                "           contact: " + ofContact(registerProfessional.getContact()) +
                "           }" +
                ") {" +
                "       id " +
                "       contact{" +
                "             email" +
                "             fax" +
                "             phone" +
                "             address{" +
                "               country" +
                "               city" +
                "               zipCode" +
                "               street" +
                "               state" +
                "             }," +
                "             name{" +
                "               first" +
                "               last" +
                "               title" +
                "               middle" +
                "             }" +
                "       }" +
                "   }";
        return publicMutationRequestBuilder(registerProfessionalMutation).header("g-recaptcha-response", RandomStringUtils.randomAlphanumeric(50));
    }

    public static MockHttpServletRequestBuilder sendWelcomeMailAgain(String id) {
        String registerProfessionalMutation = " sendWelcomeMailAgain(userId : " + of(id) + ")";
        return publicMutationRequestBuilder(registerProfessionalMutation).header("g-recaptcha-response", RandomStringUtils.randomAlphanumeric(50));
    }


    public static MockHttpServletRequestBuilder changePassword(ChangePassword changePassword) {
        String registerProfessionalMutation = " changePassword(changePassword : {" +
                "       newPassword : " + of(changePassword.getNewPassword()) + "    " +
                "       oldPassword : " + of(changePassword.getOldPassword()) + "    " +
                "})";
        return securedMutationRequestBuilder(registerProfessionalMutation);
    }

    public static MockHttpServletRequestBuilder requestChangeUsername(String newUsername, String password) {
        String registerProfessionalMutation = " requestChangeUsername(" +
                "request: {" +
                "   newUsername : " + of(newUsername) + "" +
                "   password : " + of(password) + "" +
                "   }" +
                ")";
        return securedMutationRequestBuilder(registerProfessionalMutation);
    }

    public static MockHttpServletRequestBuilder confirmUsernameChange(String token) {
        String registerProfessionalMutation = " confirmUsernameChange(token : " + of(token) + ")";
        return publicMutationRequestBuilder(registerProfessionalMutation);
    }

    public static MockHttpServletRequestBuilder requestResetPassword(String username) {
        String registerProfessionalMutation = " requestResetPassword(arg0 : " + of(username) + ")";
        return publicMutationRequestBuilder(registerProfessionalMutation).header("g-recaptcha-response", RandomStringUtils.randomAlphanumeric(50));
    }

    public static MockHttpServletRequestBuilder resetPassword(NewPassword newPassword) {
        String registerProfessionalMutation = " resetPassword(newPassword : {" +
                "       password : " + of(newPassword.getPassword()) + "    " +
                "       token : " + of(newPassword.getToken()) + "    " +
                "})";
        return publicMutationRequestBuilder(registerProfessionalMutation);
    }

    public static MockHttpServletRequestBuilder createPracticeOwnerRequest(RegisterPracticeOwner practiceOwner) {
        ContactModel contact = practiceOwner.getContact();
        RegisterPractice practice = practiceOwner.getRegisterPractice();
        String query =
                "registerPracticeOwner(" +
                        "     practiceOwner: {" +
                        "      username: " + of(practiceOwner.getUsername()) +
                        "      password:" + of(practiceOwner.getPassword()) +
                        "      contact: " + ofContact(contact) +
                        "     registerPractice:{" +
                        "       afterWorkPhone:" + of(practice.getAfterWorkPhone()) + "" +
                        "       phone:" + of(practice.getPhone()) + "" +
                        "       webSite:" + of(practice.getWebSite()) + "" +
                        "       billingAddress:" + ofAddress(practice.getBillingAddress()) + "" +
                        "       name:" + of(practice.getName()) + "" +
                        "       officeManagerName:" + of(practice.getOfficeManagerName()) + "" +
                        "       softwares:" + of(practice.getSoftwares()) + "" +
                        "       specialities:" + of(practice.getSpecialities()) +
                        "      }" +
                        "   }){" +
                        "       id " +
                        CONTACT_FRAGMENT +
                        "   }";
        return publicMutationRequestBuilder(query).header("g-recaptcha-response", RandomStringUtils.randomAlphanumeric(50));
    }

    public static MockHttpServletRequestBuilder updatePracticeOwnerGeneral(PracticeOwnerModel practiceOwnerModel, PracticeModel practice) {
        String query =
                "updatePracticeOwnerGeneral(" +
                        "      practiceOwner: {" +
                        "       id: " + of(practiceOwnerModel.getId()) +
                        "       comments: " + of(practiceOwnerModel.getComments()) +
                        "       contact: " + ofContact(practiceOwnerModel.getContact()) +
                        "      }" +
                        "       practice:{" +
                        "           afterWorkPhone:" + of(practice.getAfterWorkPhone()) + "" +
                        "           phone:" + of(practice.getPhone()) + "" +
                        "           secondEmail:" + of(practice.getSecondEmail()) + "" +
                        "           webSite:" + of(practice.getWebSite()) + "" +
                        "           billingAddress:" + ofAddress(practice.getBillingAddress()) + "" +
                        "           name:" + of(practice.getName()) + "" +
                        "           officeManagerName:" + of(practice.getOfficeManagerName()) + "" +
                        "           softwares:" + of(practice.getSoftwares()) + "" +
                        "           specialities:" + of(practice.getSpecialities()) +
                        "           id:" + of(practice.getId()) +
                        "           status:" + of(practice.getStatus()) +
                        "      }" +
                        "   )";
        return securedMutationRequestBuilder(query);
    }

    public static String toQuery(String query) {
        String baseQuery = "{\"query\":\"" +
                "   query{ %s }\"" +
                "}";
        return String.format(baseQuery, query);
    }

    public static String toMutation(String mutation) {
        String baseMutation = "{\"query\":\"" +
                "   mutation{ %s }\"" +
                "}";
        return String.format(baseMutation, mutation);
    }

    public static MockHttpServletRequestBuilder blackListPracticeLocation(String practiceLocationId) {
        String mutation = "   blackListLocation(    " +
                "       practiceLocationId: " + of(practiceLocationId) +
                " )";
        return securedMutationRequestBuilder(mutation);
    }

    public static MockHttpServletRequestBuilder unBlackListPracticeLocation(String practiceLocationId) {
        String mutation = "   unBlackListLocation(    " +
                "       practiceLocationId: " + of(practiceLocationId) +
                " )";
        return securedMutationRequestBuilder(mutation);
    }

    public static MockHttpServletRequestBuilder blackListProfessional(String professionalId) {
        String mutation = "   blackListProfessional(    " +
                "       professionalId: " + of(professionalId) +
                " )";
        return securedMutationRequestBuilder(mutation);
    }

    public static MockHttpServletRequestBuilder unBlackListProfessional(String professionalId) {
        String mutation = "   unBlackListProfessional(    " +
                "       professionalId: " + of(professionalId) +
                " )";
        return securedMutationRequestBuilder(mutation);
    }


    public static MockHttpServletRequestBuilder updateProfessionalGeneralSettingsRequest(ProfessionalModel professionalModel, ProfessionalJobPreferenceModel professionalJobPreferenceModel) {
        String updateProfessionalGeneralSettings = "   updateProfessionalGeneral(    " +
                "       professional: {" +
                "                   id: " + of(professionalModel.getId()) +
                "                   notificationsEnabled: " + professionalModel.isNotificationsEnabled() +
                "                   contact: " + ofContact(professionalModel.getContact()) +
                "                   comments: " + of(professionalModel.getComments()) +
                "               }" +
                "       jobPreference : {      " +
                "           salaryFrom: " + of(professionalJobPreferenceModel.getSalaryFrom()) + "      " +
                "           salaryTo: " + of(professionalJobPreferenceModel.getSalaryTo()) + "      " +
                "           commutingRadius: " + of(professionalJobPreferenceModel.getCommutingRadius()) + "     " +
                "           lookingForPartTimeJob: " + of(professionalJobPreferenceModel.getLookingForPartTimeJob()) + "      " +
                "           lookingForFullTimeJob: " + of(professionalJobPreferenceModel.getLookingForFullTimeJob()) + "      " +
                "           lookingForPermanentJob: " + of(professionalJobPreferenceModel.getLookingForPermanentJob()) + "      " +
                "           lookingForTemporaryJob: " + of(professionalJobPreferenceModel.getLookingForTemporaryJob()) + "      " +
                "           eveningWorkingHoursOk: " + of(professionalJobPreferenceModel.getEveningWorkingHoursOk()) + "      " +
                "           availabilityDays: " + of(professionalJobPreferenceModel.getAvailabilityDays()) + "      " +
                "           bayAreas: " + of(professionalJobPreferenceModel.getBayAreas()) + "      " +
                "           willingToRelocate: " + of(professionalJobPreferenceModel.getWillingToRelocate()) + "      " +
                "           desiredRatePerHour: " + of(professionalJobPreferenceModel.getDesiredRatePerHour()) + "    },    " +
                " )";


        return securedMutationRequestBuilder(updateProfessionalGeneralSettings);
    }

    public static MockHttpServletRequestBuilder updateProfessionalProfileRequest(String id, ProfessionalProfileModel professionalProfileModel) {
        Iterator<WorkExperienceModel> iterator = professionalProfileModel.getWorkExperiences().iterator();
        WorkExperienceModel experienceModel = iterator.next();
        WorkExperienceModel experienceModel2 = iterator.next();
        WorkExperienceModel experienceModel3 = iterator.next();

        Iterator<WorkReferenceModel> iterator2 = professionalProfileModel.getWorkReferences().iterator();
        WorkReferenceModel workReferenceModel = iterator2.next();
        WorkReferenceModel workReferenceModel2 = iterator2.next();
        WorkReferenceModel workReferenceModel3 = iterator2.next();

        String updateProfessionalProfile = "   updateProfessionalProfile(    " +
                "       professionalId : " + of(id) +
                "       profile : {      " +
                "           skillSummary: " + of(professionalProfileModel.getSkillSummary()) + "      " +
                "           education: " + of(professionalProfileModel.getEducation()) + "      " +
                "           highestDegree: " + of(professionalProfileModel.getHighestDegree()) + "     " +
                "           languages: " + of(professionalProfileModel.getLanguages()) + "      " +
                "           workExperiences: [ " +
                "                               {" +
                "                                   companyName : " + of(experienceModel.getCompanyName()) + " " +
                "                                   leaveDate : " + of(experienceModel.getLeaveDate()) + " " +
                "                                   hireDate : " + of(experienceModel.getHireDate()) + " " +
                "                                   responsibilities : " + of(experienceModel.getResponsibilities()) + " " +
                "                               } " +
                "                               {" +
                "                                   companyName : " + of(experienceModel2.getCompanyName()) + " " +
                "                                   leaveDate : " + of(experienceModel2.getLeaveDate()) + " " +
                "                                   hireDate : " + of(experienceModel2.getHireDate()) + " " +
                "                                   responsibilities : " + of(experienceModel2.getResponsibilities()) + " " +
                "                               } " +
                "                               {" +
                "                                   companyName : " + of(experienceModel3.getCompanyName()) + " " +
                "                                   leaveDate : " + of(experienceModel3.getLeaveDate()) + " " +
                "                                   hireDate : " + of(experienceModel3.getHireDate()) + " " +
                "                                   responsibilities : " + of(experienceModel3.getResponsibilities()) + " " +
                "                               } " +
                "                            ]  " +
                "           workReferences: [ " +
                "                               {" +
                "                                   email : " + of(workReferenceModel.getEmail()) + " " +
                "                                   name : " + of(workReferenceModel.getName()) + " " +
                "                                   phone : " + of(workReferenceModel.getPhone()) + " " +
                "                               } " +
                "                               {" +
                "                                   email : " + of(workReferenceModel2.getEmail()) + " " +
                "                                   name : " + of(workReferenceModel2.getName()) + " " +
                "                                   phone : " + of(workReferenceModel2.getPhone()) + " " +
                "                               } " +
                "                               {" +
                "                                   email : " + of(workReferenceModel3.getEmail()) + " " +
                "                                   name : " + of(workReferenceModel3.getName()) + " " +
                "                                   phone : " + of(workReferenceModel3.getPhone()) + " " +
                "                               } " +
                "                            ]  " +
                "               }    " +
                " )";


        return securedMutationRequestBuilder(updateProfessionalProfile);
    }

    public static MockHttpServletRequestBuilder addNotificationTemplateRequest(NotificationTemplateModel notification) {
        String request =
                "   addNotificationTemplate(" +
                        "       template: {" +
                        "           name: " + of(notification.getName()) + " " +
                        "           description: " + of(notification.getDescription()) + " " +
                        "           type: " + of(notification.getType()) + " " +
                        "           subject: " + of(notification.getSubject()) + " " +
                        "           content: " + of(notification.getContent()) + " " +
                        "           transport: " + of(notification.getTransport()) + " " +
                        "       }" +
                        "   ) { " +
                        "       id " +
                        "       name " +
                        "       description " +
                        "       type " +
                        "       subject " +
                        "       content " +
                        "       transport " +
                        "   }";

        return securedMutationRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder updateNotificationTemplateRequest(NotificationTemplateModel notification) {
        String request =
                "   updateNotificationTemplate(" +
                        "       template: {" +
                        "           id: " + of(notification.getId()) + " " +
                        "           name: " + of(notification.getName()) + " " +
                        "           description: " + of(notification.getDescription()) + " " +
                        "           type: " + of(notification.getType()) + " " +
                        "           subject: " + of(notification.getSubject()) + " " +
                        "           content: " + of(notification.getContent()) + " " +
                        "           transport: " + of(notification.getTransport()) + " " +
                        "       }" +
                        "   ) { " +
                        "       id " +
                        "       name " +
                        "       description " +
                        "       type " +
                        "       subject " +
                        "       content " +
                        "       transport " +
                        "   }";

        return securedMutationRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder deleteNotificationTemplateRequest(String id) {
        String request =
                "   deleteNotificationTemplate(" +
                        "       id: " + of(id) + " " +
                        "   )";

        return securedMutationRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder getNotificationTemplateRequest(String id) {
        String request =
                "   notificationTemplate(" +
                        "       id: " + of(id) + " " +
                        "   ) { " +
                        "       id " +
                        "       name " +
                        "       description " +
                        "       type " +
                        "       subject " +
                        "       content " +
                        "       transport " +
                        "   }";

        return securedQueryRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder listNotificationTemplateRequest(Integer page,
                                                                                Integer perPage,
                                                                                List<FindNotificationTemplatesQuery.NotificationTemplatesOrder> orders) {
        String request =
                "   notificationTemplates (" +
                        "      page:" + page +
                        "      perPage:" + perPage +
                        "      orders:" + orders +
                        "   ) { " +
                        "       count " +
                        "       nodes {" +
                        "           id " +
                        "           name " +
                        "           description " +
                        "           type " +
                        "           subject " +
                        "           content " +
                        "           transport " +
                        "       }" +
                        "   }";

        return securedQueryRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder listNotificationTypeDescriptors() {
        String request =
                "   notificationTypes" +
                        "   { " +
                        "       count " +
                        "       nodes {" +
                        "           name " +
                        "           type " +
                        "           description " +
                        "           variables {" +
                        "               name " +
                        "               variable " +
                        "           }" +
                        "       }" +
                        "    }";

        return securedQueryRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder listSystemSettings() {
        String request =
                "   systemSettings" +
                        "   { " +
                        "       count " +
                        "       nodes {" +
                        "           key " +
                        "           type " +
                        "           value " +
                        "       }" +
                        "   }";

        return securedQueryRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder updateSystemSetting(String key, String value) {
        String request =
                "   updateSystemSetting(" +
                        "       setting: {" +
                        "           key: " + of(key) +
                        "           value: " + of(value) +
                        "       }" +
                        "   )";

        return securedMutationRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder updateSystemSettings(List<SystemSettingModel> settings) {
        String request =
                "   updateSystemSettings(" +
                        "       settings: [" +
                        settings.stream().map(GraphQLRequestRepository::stringOfSystemSetting).collect(Collectors.joining(" ")) +
                        "       ]" +
                        "   )";

        return securedMutationRequestBuilder(request);
    }

    private static String stringOfSystemSetting(SystemSettingModel setting) {
        return "            {" +
                "               key: " + of(setting.getKey()) +
                "               value: " + of(setting.getValue()) +
                "           }";
    }

    public static MockHttpServletRequestBuilder getQuestionnaire(String professional, String category) {
        String request =
                "   getQuestionnaire(" +
                        "       category: " + of(category) +
                        (professional != null ? "        professional: " + of(professional) : "") +
                        "   ) {" +
                        "       ...on DentistQuestionnaireModel {" +
                        baseQuestionnaireFields() +
                        specialtyQuestionnaireFields("specialtiesComfort") +
                        "           temporaryAsRdh" +
                        "           cadCam" +
                        "           intraOralCam" +
                        "           pano" +
                        "           surgery" +
                        "           hoursOnFeet" +
                        "           patientsPerDay" +
                        "       }" +
                        "       ...on FrontOfficeQuestionnaireModel {" +
                        baseQuestionnaireFields() +
                        specialtyQuestionnaireFields("specialtiesFamiliarity") +
                        dutiesQuestionnaireFields() +
                        "           xRaysAndCameraImagesToInsurance" +
                        "           crossTrained" +
                        "       }" +
                        "       ...on HygienistQuestionnaireModel {" +
                        baseQuestionnaireFields() +
                        specialtyQuestionnaireFields("specialtiesFamiliarity") +
                        "           nitrousOxide" +
                        "           anesthetize" +
                        "           antiMicrobial" +
                        "           intraOralCam" +
                        "           pano" +
                        "           recareAppt" +
                        "       }" +
                        "       ...on AssistantQuestionnaireModel {" +
                        baseQuestionnaireFields() +
                        specialtyQuestionnaireFields("specialtiesFamiliarity") +
                        dutiesQuestionnaireFields() +
                        "           cadCam" +
                        "           imaging3D" +
                        "           xray" +
                        "           intraOralCam" +
                        "           pano" +
                        "           nomad" +
                        "           crossTrained" +
                        "       }" +
                        "}";

        return securedQueryRequestBuilder(request);
    }

    private static String baseQuestionnaireFields() {
        return
                "           id" +
                        "           yoeInDental" +
                        "           yoeBySpecialty" +
                        "           digitalRadiographySystems" +
                        "           managementSoftware";
    }

    private static String specialtyQuestionnaireFields(String fieldName) {
        return
                "           " + fieldName + " {" +
                        "               pedo" +
                        "               prostho" +
                        "               perio" +
                        "               endo" +
                        "               general" +
                        "               cosmetic" +
                        "               implants" +
                        "               oralSurgery" +
                        "           }";
    }

    private static String dutiesQuestionnaireFields() {
        return
                "           duties {" +
                        "               insuranceBilling" +
                        "               eligibilityVerification" +
                        "               patientScheduling" +
                        "               hygieneRecall" +
                        "               acctReceivable" +
                        "               claimSubmission" +
                        "               insurancePaymentCollection" +
                        "               patientCoordination" +
                        "               posting" +
                        "               acctPayable" +
                        "               collections" +
                        "               treatmentPlanning" +
                        "               treatmentPresentation" +
                        "               marketingSocialIntegration" +
                        "               financialCoordination" +
                        "               payroll" +
                        "               officeManagement" +
                        "           }";
    }

    public static MockHttpServletRequestBuilder editDentistQuestionaire(DentistQuestionnaireModel model) {
        String request =
                "   editDentistQuestionnaire(" +
                        "       questionnaire: {" +
                        baseQuestionnaireFields(model) +
                        specialtyComfortQuestionnaireFields(model.getSpecialtiesComfort()) +
                        "           temporaryAsRdh: " + model.getTemporaryAsRdh() +
                        "           cadCam: " + model.getCadCam() +
                        "           intraOralCam: " + model.getIntraOralCam() +
                        "           pano: " + model.getPano() +
                        "           surgery: " + model.getSurgery() +
                        "           hoursOnFeet: " + model.getHoursOnFeet() +
                        "           patientsPerDay: " + model.getPatientsPerDay() +
                        "       }" +
                        "   )";

        return securedMutationRequestBuilder(request);
    }

    public static MockHttpServletRequestBuilder editFrontOfficeQuestionnaire(FrontOfficeQuestionnaireModel model) {
        String request =
                "   editFrontOfficeQuestionnaire(" +
                        "       questionnaire: {" +
                        baseQuestionnaireFields(model) +
                        specialtyFamiliarityQuestionnaireFields(model.getSpecialtiesFamiliarity()) +
                        "           xRaysAndCameraImagesToInsurance: " + model.getxRaysAndCameraImagesToInsurance() +
                        "           crossTrained: " + model.getCrossTrained() +
                        "       }" +
                        "   )";

        return securedMutationRequestBuilder(request);
    }

    private static String baseQuestionnaireFields(QuestionnaireModel model) {
        return
                "           id: " + of(model.getId()) +
                        "           yoeInDental: " + model.getYoeInDental() +
                        "           yoeBySpecialty: " + model.getYoeBySpecialty() +
                        "           digitalRadiographySystems: " + of(model.getDigitalRadiographySystems()) +
                        "           managementSoftware: " + of(model.getManagementSoftware());
    }

    private static String specialtyComfortQuestionnaireFields(SpecialtyComfortLevelModel model) {
        return
                "           specialtiesComfort: {" +
                        "               pedo: " + model.getPedo() +
                        "               prostho: " + model.getProstho() +
                        "               perio: " + model.getPerio() +
                        "               endo: " + model.getEndo() +
                        "               general: " + model.getGeneral() +
                        "               cosmetic: " + model.getCosmetic() +
                        "               implants: " + model.getImplants() +
                        "               oralSurgery: " + model.getOralSurgery() +
                        "           }";
    }

    private static String specialtyFamiliarityQuestionnaireFields(SpecialtyFamiliarityModel model) {
        return
                "           specialtiesFamiliarity: {" +
                        "               pedo: " + model.getPedo() +
                        "               prostho: " + model.getProstho() +
                        "               perio: " + model.getPerio() +
                        "               endo: " + model.getEndo() +
                        "               general: " + model.getGeneral() +
                        "               cosmetic: " + model.getCosmetic() +
                        "               implants: " + model.getImplants() +
                        "               oralSurgery: " + model.getOralSurgery() +
                        "           }";
    }

    private static MockHttpServletRequestBuilder getMockHttpServletRequestBuilder(String content, String apiPath) {
        return MockMvcRequestBuilders
                .post(apiPath)
                .accept(MediaType.APPLICATION_JSON).content(content)
                .contentType(MediaType.APPLICATION_JSON);
    }

    public static MockHttpServletRequestBuilder publicQueryRequestBuilder(String content) {
        return getMockHttpServletRequestBuilder(toQuery(content), API_PATH);
    }

    public static MockHttpServletRequestBuilder publicMutationRequestBuilder(String content) {
        return getMockHttpServletRequestBuilder(toMutation(content), API_PATH);
    }

    public static MockHttpServletRequestBuilder securedQueryRequestBuilder(String content) {
        return getMockHttpServletRequestBuilder(toQuery(content), SECURED_API_PATH);
    }

    public static MockHttpServletRequestBuilder securedMutationRequestBuilder(String content) {
        return getMockHttpServletRequestBuilder(toMutation(content), SECURED_API_PATH);
    }

    public static String of(Object content) {
        if (content instanceof Boolean) {
            return content.toString();
        }
        if (content instanceof Collection) {
            Collection collection = (Collection) content;
            Collection collect = (Collection) collection.stream().map(o -> wrap(o)).collect(Collectors.toList());
            return "[" + String.join(",", collect) + "]";
        }
        return wrap(content);
    }

    private static String wrap(Object content) {
        String separator = "\\\"";
        return content == null ? null : separator + content + separator;
    }
}
