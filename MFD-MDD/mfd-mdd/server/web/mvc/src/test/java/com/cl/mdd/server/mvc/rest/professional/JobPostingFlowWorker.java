package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.mvc.rest.NotificationServiceAwareWorker;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.service.notification.JobPostingApplicationStartDateTimeVariables.DATE_FORMATTER;
import static com.cl.mdd.server.core.service.notification.JobPostingApplicationStartDateTimeVariables.TIME_FORMATTER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Component
public class JobPostingFlowWorker extends NotificationServiceAwareWorker {

    public static final String JOB_POSTING_CANCELLED_FOR_APPLICANT = "JOB_POSTING_CANCELLED_FOR_APPLICANT";

    public static final String JOB_POSTING_CANCELLED_FOR_SYSTEM_USER = "JOB_POSTING_CANCELLED_FOR_SYSTEM_USER";

    public static final String JOB_POSTING_UPDATED_FOR_CANCELLED_APPLICANTS = "JOB_POSTING_UPDATED_FOR_CANCELLED_APPLICANTS";

    public static final String JOB_POSTING_UPDATED_FOR_SYSTEM_USER = "JOB_POSTING_UPDATED_FOR_SYSTEM_USER";

    private static final String JOB_POSTING_APPLICATION_CREATED = "JOB_POSTING_APPLICATION_CREATED";

    private static final String JOB_POSTING_APPLICATION_WITHDRAWN = "JOB_POSTING_APPLICATION_WITHDRAWN";

    private static final String JOB_POSTING_APPLICATION_ACCEPTED_FOR_PRACTICE_OWNER = "JOB_POSTING_APPLICATION_ACCEPTED_FOR_PRACTICE_OWNER";

    private static final String JOB_POSTING_APPLICATION_ACCEPTED_FOR_PROFESSIONAL = "JOB_POSTING_APPLICATION_ACCEPTED_FOR_PROFESSIONAL";

    private static final String JOB_POSTING_APPLICATION_CANCELLED_FOR_PRACTICE_OWNER = "JOB_POSTING_APPLICATION_CANCELLED_FOR_PRACTICE_OWNER";

    private static final String JOB_POSTING_APPLICATION_CANCELLED_FOR_PROFESSIONAL = "JOB_POSTING_APPLICATION_CANCELLED_FOR_PROFESSIONAL";

    private static final String JOB_POSTING_APPLICATION_ACCEPTED_FOR_CONCURRENT_APPLICANT = "JOB_POSTING_APPLICATION_ACCEPTED_FOR_CONCURRENT_APPLICANT";

    private static final String JOB_POSTING_APPLICATION_REJECTED_FOR_PRACTICE_OWNER = "JOB_POSTING_APPLICATION_REJECTED_FOR_PRACTICE_OWNER";

    private static final String JOB_POSTING_APPLICATION_REJECTED_FOR_CONCURRENT_APPLICANT = "JOB_POSTING_APPLICATION_REJECTED_FOR_CONCURRENT_APPLICANT";

    private static final String JOB_POSTING_APPLICATION_BOOKED_FOR_PROFESSIONAL = "JOB_POSTING_APPLICATION_BOOKED_FOR_PROFESSIONAL";

    private static final String JOB_POSTING_APPLICATION_BOOKED_FOR_SYSTEM_USER = "JOB_POSTING_APPLICATION_BOOKED_FOR_SYSTEM_USER";

    private static final String JOB_POSTING_PUBLISHED_FOR_PROFESSIONAL = "JOB_POSTING_PUBLISHED_FOR_PROFESSIONAL";

    private static final String JOB_POSTING_PUBLISHED_FOR_SYSTEM_USER = "JOB_POSTING_PUBLISHED_FOR_SYSTEM_USER";

    private static final String JOB_POSTING_DELETED = "JOB_POSTING_DELETED";

    public void assertPracticeOwnerNotifiedAboutApplicationCreation(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_CREATED, placeHolders -> {
            assertThat(placeHolders.size(), is(11));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, practiceOwner.getUsername(), practiceOwner.getContact().getPhone());
    }

    public void assertPracticeOwnerNotifiedAboutApplicationWithdraw(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_WITHDRAWN, placeHolders -> {
            assertThat(placeHolders.size(), is(9));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            return null;
        }, practiceOwner.getUsername(), practiceOwner.getContact().getPhone());
    }

    public void assertPracticeOwnerNotifiedAboutApplicationAccept(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_ACCEPTED_FOR_PRACTICE_OWNER, placeHolders -> {
            assertThat(placeHolders.size(), is(10));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, practiceOwner.getUsername(), practiceOwner.getContact().getPhone());
    }

    public void assertProfessionalNotifiedAboutApplicationAccept(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate, ZonedDateTime endDate) {
        assertNotification(JOB_POSTING_APPLICATION_ACCEPTED_FOR_PROFESSIONAL, (placeHolders) -> {
            assertThat(placeHolders.size(), is(19));

            AddressModel addressModel = practiceLocation.getContact().getAddress();
            assertThat(placeHolders.get("{practice.location.state}"), is(addressModel.getState()));
            assertThat(placeHolders.get("{practice.location.city}"), is(addressModel.getCity()));
            assertThat(placeHolders.get("{practice.location.street}"), is(addressModel.getStreet()));
            assertThat(placeHolders.get("{practice.location.zip_code}"), is(addressModel.getZipCode()));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.end.date}"), is(DATE_FORMATTER.format(endDate)));
            assertThat(placeHolders.get("{job.posting.application.end.time}"), is(TIME_FORMATTER.format(endDate)));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));

            assertTrue(placeHolders.containsKey("{professional.preferences.rate}"));
            assertThat(placeHolders.get("{practice.location.phonenumber}"), is(practiceLocation.getContact().getPhone()));

            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, professional.getUsername(), professional.getContact().getPhone());
    }

    public void assertPracticeOwnerNotifiedAboutApplicationCancel(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties) {
        assertNotification(JOB_POSTING_APPLICATION_CANCELLED_FOR_PRACTICE_OWNER, placeHolders -> {
            assertThat(placeHolders.size(), is(9));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, practiceOwner.getUsername(), practiceOwner.getContact().getPhone());
    }

    public void assertPracticeOwnerNotifiedAboutApplicationRejection(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_REJECTED_FOR_PRACTICE_OWNER, placeHolders -> {
            assertThat(placeHolders.size(), is(11));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, practiceOwner.getUsername(), practiceOwner.getContact().getPhone());
    }

    public void assertPracticeOwnerNotifiedAboutDeletedPosting(RegisterPracticeOwner practiceOwner, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_DELETED, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{job.posting.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));

            return null;
        }, practiceOwner.getUsername(), practiceOwner.getContact().getPhone());
    }


    private void assertNotification(String temporaryJobPostingApplicationCreated, Function<Map<String, String>, String> assertPlaceholders, String email, String phone) {
        assertSnsRequest(temporaryJobPostingApplicationCreated, email, phone, assertPlaceholders);
    }

    public void assertProfessionalNotifiedAboutPublishedJobPosting(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_PUBLISHED_FOR_PROFESSIONAL, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, proAccount.getUsername(), proAccount.getContact().getPhone());
    }

    public void assertSystemUserNotifiedAboutPublishedJobPosting(RegisterPracticeOwner practiceOwner, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertSnsRequest(JOB_POSTING_PUBLISHED_FOR_SYSTEM_USER, "iana@mdd.com", "1234567890", placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{job.posting.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.start.time}"), is(TIME_FORMATTER.format(startDate)));
            return null;
        });
    }

    public void assertProfessionalNotifiedAboutCancelledApplication(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_CANCELLED_FOR_PROFESSIONAL, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, proAccount.getUsername(), proAccount.getContact().getPhone());
    }

    public void assertProfessionalNotifiedAboutBookedApplication(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_BOOKED_FOR_PROFESSIONAL, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, proAccount.getUsername(), proAccount.getContact().getPhone());
    }

    public void assertSystemUserNotifiedAboutBookedApplication(RegisterPracticeOwner practiceOwner, RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertSnsRequest(JOB_POSTING_APPLICATION_BOOKED_FOR_SYSTEM_USER, "iana@mdd.com", "1234567890", placeHolders -> {
            assertThat(placeHolders.size(), is(10));
            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            return null;
        });
    }

    public void assertProfessionalNotifiedAboutCancelledJobPosting(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_CANCELLED_FOR_APPLICANT, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{job.posting.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, proAccount.getUsername(), proAccount.getContact().getPhone());
    }

    public void assertProfessionalNotifiedAcceptedJobPostingApplication(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_ACCEPTED_FOR_CONCURRENT_APPLICANT, placeHolders -> {
            assertThat(placeHolders.size(), is(7));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            return null;
        }, proAccount.getUsername(), proAccount.getContact().getPhone());
    }

    public void assertProfessionalNotifiedRejectedJobPostingApplication(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_APPLICATION_REJECTED_FOR_CONCURRENT_APPLICANT, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.application.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.application.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, proAccount.getUsername(), proAccount.getContact().getPhone());
    }

    public void assertSystemUserNotifiedAboutCancelledJobPosting(RegisterPracticeOwner practiceOwner, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertSnsRequest(JOB_POSTING_CANCELLED_FOR_SYSTEM_USER, "iana@mdd.com", "1234567890", placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            return null;
        });
    }

    public void assertProfessionalNotifiedAboutUpdateJobPosting(RegisterProfessional proAccount, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(JOB_POSTING_UPDATED_FOR_CANCELLED_APPLICANTS, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{professional.first.name}"), is(proAccount.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(proAccount.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.start.time}"), is(TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        }, proAccount.getUsername(), proAccount.getContact().getPhone());
    }

    public void assertSystemNotifiedAboutUpdateJobPosting(RegisterPracticeOwner practiceOwner, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertSnsRequest(JOB_POSTING_UPDATED_FOR_SYSTEM_USER, "iana@mdd.com", "1234567890", placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{job.posting.start.date}"), is(DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.start.time}"), is(TIME_FORMATTER.format(startDate)));
            return null;
        });
    }
}
