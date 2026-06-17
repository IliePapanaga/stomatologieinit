package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.RegisterUser;
import com.cl.mdd.server.core.service.notification.JobDayStartDateTimeVariables;
import com.cl.mdd.server.core.service.notification.JobPostingApplicationStartDateTimeVariables;
import com.cl.mdd.server.mvc.rest.NotificationServiceAwareWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.service.notification.AttendanceAlertReplyVariables.TEMPLATES_MAPPING;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Component
public class JobAttendanceFlowWorker extends NotificationServiceAwareWorker {

    public static final String ATTENDANCE_SOS_REQUESTED_FOR_SYSTEM = "ATTENDANCE_SOS_REQUESTED_FOR_SYSTEM";

    public static final String ALERTED_ATTENDANCE_FOR_EMPLOYEE = "ALERTED_ATTENDANCE_FOR_EMPLOYEE";

    public static final String ALERT_ATTENDANCE_REPLIED_FOR_EMPLOYER = "ALERT_ATTENDANCE_REPLIED_FOR_EMPLOYER";

    public static final String CHECKED_IN_ATTENDANCE_FOR_EMPLOYEE = "CHECKED_IN_ATTENDANCE_FOR_EMPLOYEE";

    public static final String CHECKED_IN_ATTENDANCE_FOR_SYSTEM_USER = "CHECKED_IN_ATTENDANCE_FOR_SYSTEM_USER";

    public static final String NO_SHOW_ATTENDANCE_FOR_EMPLOYEE = "NO_SHOW_ATTENDANCE_FOR_EMPLOYEE";

    public static final String NO_SHOW_ATTENDANCE_FOR_SYSTEM_USER = "NO_SHOW_ATTENDANCE_FOR_SYSTEM_USER";

    private static final String REJECTED_ATTENDANCE_FOR_SYSTEM_USER = "REJECTED_ATTENDANCE_FOR_SYSTEM_USER";

    private static final String REJECTED_ATTENDANCE_FOR_EMPLOYEE = "REJECTED_ATTENDANCE_FOR_EMPLOYEE";

    public static final String WORK_START_SOON_FOR_EMPLOYEE = "WORK_START_SOON_FOR_EMPLOYEE";

    public static final String WORK_STARTED_FOR_EMPLOYEE = "WORK_STARTED_FOR_EMPLOYEE";

    public static final String WORK_STARTED_FOR_SYSTEM = "WORK_STARTED_FOR_SYSTEM";

    private static final String PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_PROFESSIONAL = "PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_PROFESSIONAL";

    private static final String PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_SYSTEM = "PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_SYSTEM";

    private static final String PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_PRACTICE_OWNER = "PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_PRACTICE_OWNER";

    private static final String PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_SYSTEM = "PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_SYSTEM";

    @Value("${aws.mail.sender}")
    private String awsMailSender;

    private void assertNotification(RegisterUser registerUser, String type, Function<Map<String, String>, String> contextProcessing) {
        assertSnsRequest(type, registerUser.getUsername(), registerUser.getContact().getPhone(), contextProcessing);
    }

    public void assertSystemUserNotifiedPracticeOwnerWouldHire(RegisterPracticeOwner practiceOwner, RegisterProfessional proAccount, PracticeLocationModel practiceLocation) {
        systemNotified(PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_SYSTEM, placeHolders -> {
            assertThat(placeHolders.size(), is(6));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            return null;
        });
    }

    public void assertSystemUserNotifiedProfessionalWouldWork(RegisterPracticeOwner practiceOwner, RegisterProfessional proAccount, PracticeLocationModel practiceLocation) {
        systemNotified(PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_SYSTEM, placeHolders -> {
            assertThat(placeHolders.size(), is(6));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            return null;
        });
    }

    public void assertProfessionalNotifiedWouldHire(RegisterProfessional proAccount, PracticeLocationModel practiceLocation) {
        assertNotification(proAccount, PRACTICE_OWNER_WOULD_HIRE_PROFESSIONAL_PERMANENTLY_FOR_PROFESSIONAL, placeHolders -> {
            assertThat(placeHolders.size(), is(4));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertPracticeOwnerNotifiedWouldWork(RegisterPracticeOwner practiceOwner, RegisterProfessional proAccount, PracticeLocationModel practiceLocation) {
        assertNotification(practiceOwner, PROFESSIONAL_WOULD_WORK_PERMANENTLY_FOR_PRACTICE_OWNER, placeHolders -> {
            assertThat(placeHolders.size(), is(7));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertSystemUserNotifiedSosRequest(RegisterPracticeOwner practiceOwner, RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        systemNotified(ATTENDANCE_SOS_REQUESTED_FOR_SYSTEM, placeHolders -> {
            assertThat(placeHolders.size(), is(10));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.day.start.date}"), is(JobDayStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.day.start.time}"), is(JobDayStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            return null;
        });
    }

    public void assertProfessionalNotifiedAlert(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(proAccount, ALERTED_ATTENDANCE_FOR_EMPLOYEE, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.day.start.date}"), is(JobDayStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.day.start.time}"), is(JobDayStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertPracticeOwnerNotifiedAlert(RegisterPracticeOwner practiceOwner, RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String template,
                                                 String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(practiceOwner, ALERT_ATTENDANCE_REPLIED_FOR_EMPLOYER, placeHolders -> {
            assertThat(placeHolders.size(), is(12));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{attendance.alert.reply.message}"), is(TEMPLATES_MAPPING.get(template)));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{job.day.start.date}"), is(JobDayStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.day.start.time}"), is(JobDayStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertPracticeOwnerNotifiedCheckIn(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        systemNotified(CHECKED_IN_ATTENDANCE_FOR_SYSTEM_USER, placeHolders -> {
            assertThat(placeHolders.size(), is(7));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.day.start.date}"), is(JobDayStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.day.start.time}"), is(JobDayStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            return null;
        });
    }

    public void assertProfessionalNotifiedCheckIn(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(proAccount, CHECKED_IN_ATTENDANCE_FOR_EMPLOYEE, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.day.start.date}"), is(JobDayStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.day.start.time}"), is(JobDayStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertSystemNotifiedNoShow(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        systemNotified(NO_SHOW_ATTENDANCE_FOR_SYSTEM_USER, placeHolders -> {
            assertThat(placeHolders.size(), is(7));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.day.start.date}"), is(JobDayStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.day.start.time}"), is(JobDayStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            return null;
        });
    }

    public void assertProfessionalNotifiedNoShow(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, ZonedDateTime startDate) {
        assertNotification(proAccount, NO_SHOW_ATTENDANCE_FOR_EMPLOYEE, placeHolders -> {
            assertThat(placeHolders.size(), is(6));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.day.start.date}"), is(JobDayStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.day.start.time}"), is(JobDayStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertSystemNotifiedRejected(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        systemNotified(REJECTED_ATTENDANCE_FOR_SYSTEM_USER, placeHolders -> {
            assertThat(placeHolders.size(), is(7));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(JobPostingApplicationStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(JobPostingApplicationStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            return null;
        });
    }

    public void assertProfessionalNotifiedRejected(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(proAccount, REJECTED_ATTENDANCE_FOR_EMPLOYEE, placeHolders -> {
            assertThat(placeHolders.size(), is(7));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(JobPostingApplicationStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(JobPostingApplicationStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            return null;
        });
    }

    public void assertProfessionalNotifiedWorkStartSoon(RegisterProfessional proAccount, RegisterPracticeOwner practiceOwner, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate, ZonedDateTime endDate) {
        assertNotification(proAccount, WORK_START_SOON_FOR_EMPLOYEE, placeHolders -> {
            assertThat(placeHolders.size(), is(13));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(JobPostingApplicationStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(JobPostingApplicationStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.end.date}"), is(JobPostingApplicationStartDateTimeVariables.DATE_FORMATTER.format(endDate)));
            assertThat(placeHolders.get("{job.posting.application.end.time}"), is(JobPostingApplicationStartDateTimeVariables.TIME_FORMATTER.format(endDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertProfessionalNotifiedWorkStarted(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(proAccount, WORK_STARTED_FOR_EMPLOYEE, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(JobPostingApplicationStartDateTimeVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(JobPostingApplicationStartDateTimeVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertSystemUserNotifiedWorkStarted(PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties) {
        systemNotified(WORK_STARTED_FOR_SYSTEM, placeHolders -> {
            assertThat(placeHolders.size(), is(3));

            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            return null;
        });
    }

    private void systemNotified(String type, Function<Map<String, String>, String> contextProcessing) {
        assertSnsRequest(type, "iana@mdd.com", "1234567890", contextProcessing);
    }
}
