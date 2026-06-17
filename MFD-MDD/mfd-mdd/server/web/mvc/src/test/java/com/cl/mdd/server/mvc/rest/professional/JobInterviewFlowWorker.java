package com.cl.mdd.server.mvc.rest.professional;

import com.cl.mdd.server.core.data.model.PracticeLocationModel;
import com.cl.mdd.server.core.data.model.RegisterPracticeOwner;
import com.cl.mdd.server.core.data.model.RegisterProfessional;
import com.cl.mdd.server.core.data.model.RegisterUser;
import com.cl.mdd.server.core.data.model.common.AddressModel;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.service.TransactionHelper;
import com.cl.mdd.server.core.service.notification.JobInterviewStartDateVariables;
import com.cl.mdd.server.mvc.rest.NotificationServiceAwareWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Component
public class JobInterviewFlowWorker extends NotificationServiceAwareWorker {


    private static final String JOB_INTERVIEW_ACCEPTED_FOR_PRACTICE_OWNER = "JOB_INTERVIEW_ACCEPTED_FOR_PRACTICE_OWNER";

    private static final String JOB_INTERVIEW_ACCEPTED_FOR_PROFESSIONAL = "JOB_INTERVIEW_ACCEPTED_FOR_PROFESSIONAL";

    private static final String JOB_INTERVIEW_CANCELLED = "JOB_INTERVIEW_CANCELLED";

    private static final String JOB_INTERVIEW_FINISHED = "JOB_INTERVIEW_FINISHED";

    private static final String JOB_INTERVIEW_REJECTED = "JOB_INTERVIEW_REJECTED";

    private static final String JOB_INTERVIEW_SCHEDULED = "JOB_INTERVIEW_SCHEDULED";

    private static final String JOB_INTERVIEW_SCHEDULED_REPEATEDLY = "JOB_INTERVIEW_SCHEDULED_REPEATEDLY";

    private static final String JOB_INTERVIEW_START_SOON = "JOB_INTERVIEW_START_SOON";

    private static final String INTERVIEW_INDEX = "INTERVIEW_INDEX";

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private TransactionHelper transactionHelper;

    @Value("${aws.mail.sender}")
    private String awsMailSender;

    private void assertNotification(RegisterUser registerUser, String type, Function<Map<String, String>, String> assertPlaceHolders) {
        assertSnsRequest(type, registerUser.getUsername(), registerUser.getContact().getPhone(), assertPlaceHolders);
    }

    public void assertPracticeOwnerNotifiedAboutAcceptedInterview(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation,
                                                                  String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(practiceOwner, JOB_INTERVIEW_ACCEPTED_FOR_PRACTICE_OWNER, placeHolders -> {
            assertThat(placeHolders.size(), is(11));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.interview.start.date}"), is(JobInterviewStartDateVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.interview.start.time}"), is(JobInterviewStartDateVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertProfessionalNotifiedAboutAcceptedInterview(RegisterProfessional professional, RegisterPracticeOwner practiceOwner, PracticeLocationModel practiceLocation, AddressModel practiceLocationAddress,
                                                                 String jobPostingName, Set<String> specialties, ZonedDateTime startDate) throws Exception {
        assertNotification(professional, JOB_INTERVIEW_ACCEPTED_FOR_PROFESSIONAL, placeHolders -> {
            assertThat(placeHolders.size(), is(15));

            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.interview.start.date}"), is(JobInterviewStartDateVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.interview.start.time}"), is(JobInterviewStartDateVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{practice.location.state}"), is(practiceLocationAddress.getState()));
            assertThat(placeHolders.get("{practice.location.city}"), is(practiceLocationAddress.getCity()));
            assertThat(placeHolders.get("{practice.location.street}"), is(practiceLocationAddress.getStreet()));
            assertThat(placeHolders.get("{practice.location.zip_code}"), is(practiceLocationAddress.getZipCode()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertPracticeOwnerNotifiedAboutRejectedInterview(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation,
                                                                  String jobPostingName, Set<String> specialties) {
        assertNotification(practiceOwner, JOB_INTERVIEW_REJECTED, placeHolders -> {
            assertThat(placeHolders.size(), is(8));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            return null;
        });
    }

    public void assertProfessionalNotifiedAboutCancelledInterview(RegisterProfessional professional, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties) {
        assertNotification(professional, JOB_INTERVIEW_CANCELLED, placeHolders -> {
            assertThat(placeHolders.size(), is(6));

            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertProfessionalNotifiedAboutScheduledInterview(RegisterProfessional professional, PracticeLocationModel practiceLocation, AddressModel addressModel,
                                                                  String jobPostingName, Set<String> specialties) {
        assertNotification(professional, JOB_INTERVIEW_SCHEDULED, placeHolders -> {
            assertThat(placeHolders.size(), is(10));

            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{practice.location.state}"), is(addressModel.getState()));
            assertThat(placeHolders.get("{practice.location.city}"), is(addressModel.getCity()));
            assertThat(placeHolders.get("{practice.location.street}"), is(addressModel.getStreet()));
            assertThat(placeHolders.get("{practice.location.zip_code}"), is(addressModel.getZipCode()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }


    private void systemNotified(String type) {
        assertSnsRequest(type, "iana@mdd.com", "1234567890", placeHolders -> {
            assertThat(placeHolders.size(), is(0));
            return null;
        });
    }

    public void assertProfessionalNotifiedAboutInterviewStartSoon(RegisterProfessional professional, RegisterPracticeOwner practiceOwner, PracticeLocationModel practiceLocation, AddressModel addressModel,
                                                                  String jobPostingName, Set<String> specialties, ZonedDateTime startDate) {
        assertNotification(professional, JOB_INTERVIEW_START_SOON, placeHolders -> {
            assertThat(placeHolders.size(), is(15));

            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{job.posting.interview.start.date}"), is(JobInterviewStartDateVariables.DATE_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{job.posting.interview.start.time}"), is(JobInterviewStartDateVariables.TIME_FORMATTER.format(startDate)));
            assertThat(placeHolders.get("{practice.location.state}"), is(addressModel.getState()));
            assertThat(placeHolders.get("{practice.location.city}"), is(addressModel.getCity()));
            assertThat(placeHolders.get("{practice.location.street}"), is(addressModel.getStreet()));
            assertThat(placeHolders.get("{practice.location.zip_code}"), is(addressModel.getZipCode()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }


    public void assertPracticeOwnerNotifiedAboutFinishedInterview(RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation,
                                                                  String jobPostingName, Set<String> specialties) {
        assertNotification(practiceOwner, JOB_INTERVIEW_FINISHED, placeHolders -> {
            assertThat(placeHolders.size(), is(9));

            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));
            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{mdd.admin.email}"), is(awsMailSender));
            return null;
        });
    }

    public void assertSystemNotifiedRepeatedlyScheduledInterview(int count, RegisterPracticeOwner practiceOwner, RegisterProfessional professional, PracticeLocationModel practiceLocation, String jobPostingName, Set<String> specialties) {
        assertSnsRequest(JOB_INTERVIEW_SCHEDULED_REPEATEDLY, "iana@mdd.com", "1234567890", placeHolders -> {
            assertThat(placeHolders.size(), is(9));

            assertThat(placeHolders.get("{professional.first.name}"), is(professional.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{professional.last.name}"), is(professional.getContact().getName().getLast()));
            assertThat(placeHolders.get("{job.posting.name}"), is(jobPostingName));
            assertThat(placeHolders.get("{job.posting.specialties}"), is(specialties.stream().sorted().collect(Collectors.joining(", "))));
            assertThat(placeHolders.get("{practice.location.name}"), is(practiceLocation.getName()));
            assertThat(placeHolders.get("{client.first.name}"), is(practiceOwner.getContact().getName().getFirst()));
            assertThat(placeHolders.get("{client.last.name}"), is(practiceOwner.getContact().getName().getLast()));
            assertThat(placeHolders.get("{client.phone.number}"), is(practiceOwner.getContact().getPhone()));

            assertThat(placeHolders.get(INTERVIEW_INDEX), is(String.valueOf(count)));
            return null;
        });
    }
}
