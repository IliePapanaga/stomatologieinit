package com.cl.mdd.server.core.manager.payment;

import com.cl.mdd.server.core.data.model.payment.PaymentPermanentInfo;
import com.cl.mdd.server.core.data.persistent.access.payment.PaymentDao;
import com.cl.mdd.server.core.data.persistent.access.posting.CheckInDao;
import com.cl.mdd.server.core.data.persistent.access.posting.JobInterviewDao;
import com.cl.mdd.server.core.data.persistent.access.posting.PermanentJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.model.payment.Payment;
import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.PermanentJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.WorkSchedule;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.CheckIn;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.manager.annotation.Manager;
import com.cl.mdd.server.core.settings.Settings;
import com.cl.mdd.server.core.settings.SystemSettings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;

import static com.cl.mdd.server.core.settings.Settings.FeePermanentJobSettings.PERCENT_FOR_HIRING_DENTISTS;
import static com.cl.mdd.server.core.settings.Settings.FeePermanentJobSettings.PERCENT_FOR_HIRING_OTHERS;
import static java.util.stream.Collectors.joining;

/**
 * Payment-related operations for the service layer.
 */
@Manager
public class PaymentManager {

    static final long DEFAULT_FEE = 50;

    @Value("${specialties.rdh.id:HYGIENISTS}")
    private String SPEC_HYG = "HYGIENISTS";
    @Value("${specialties.rda.id:ASSISTANTS}")
    private String SPEC_ASSIST = "ASSISTANTS";
    @Value("${specialties.frontoffice.id:FRONT_OFFICE_PERSONNEL}")
    private String SPEC_FO = "FRONT_OFFICE_PERSONNEL";
    @Value("${specialties.dds.id:DENTISTS}")
    private String SPEC_DEN = "DENTISTS";
    @Value("${specialties.dds.anesth.id:DENTAL_ANESTHESIOLOGIST}")
    private String SPEC_DEN_ANESTH = "DENTAL_ANESTHESIOLOGIST";
    @Value("${specialties.dds.general.id:GENERAL_DENTIST}")
    private String SPEC_DEN_GEN = "GENERAL_DENTIST";

    private final CheckInDao checkInDao;
    private final JobInterviewDao jobInterviewDao;
    private final PermanentJobPostingApplicationDao permanentApplicationDao;
    private final PaymentDao paymentDao;
    private final SystemSettings systemSettings;

    @Autowired
    public PaymentManager(
            CheckInDao checkInDao, JobInterviewDao jobInterviewDao,
            PermanentJobPostingApplicationDao permanentApplicationDao, PaymentDao paymentDao,
            SystemSettings systemSettings) {
        this.checkInDao = checkInDao;
        this.jobInterviewDao = jobInterviewDao;
        this.permanentApplicationDao = permanentApplicationDao;
        this.paymentDao = paymentDao;
        this.systemSettings = systemSettings;
    }

    SubCategory pickSpecialty(Collection<SubCategory> subCategories) {
        return subCategories.iterator().next();
    }

    protected BigDecimal rate(SubCategory subCategory) {
        long fee = DEFAULT_FEE;
        if(StringUtils.equalsIgnoreCase(subCategory.getCategory().getId(), SPEC_HYG)) {
            fee = systemSettings.get(Settings.FeeTemporaryJobSettings.COMPENSATION_RDH);
        }
        else if(StringUtils.equalsIgnoreCase(subCategory.getCategory().getId(), SPEC_ASSIST) // front desk and assistants together
                || StringUtils.equalsIgnoreCase(subCategory.getCategory().getId(), SPEC_FO)) {
            fee = systemSettings.get(Settings.FeeTemporaryJobSettings.COMPENSATION_RDA);
        }
        else if(StringUtils.equalsAny(subCategory.getId(), SPEC_DEN_ANESTH, SPEC_DEN_GEN)) {
            fee = systemSettings.get(Settings.FeeTemporaryJobSettings.COMPENSATION_DDS);
        }
        else if(StringUtils.equalsIgnoreCase(subCategory.getCategory().getId(), SPEC_DEN)) {
            fee = systemSettings.get(Settings.FeeTemporaryJobSettings.COMPENSATION_SPECIALIST);
        }
        return new BigDecimal(fee);
    }

    protected BigDecimal rate(JobPosting posting) {
        return rate(pickSpecialty(posting.getSubCategories()));
    }

    Payment createPayment(JobPosting posting, BigDecimal rate, Professional pro, Consumer<Payment> setOrigin) {
        Payment payment = new Payment();
        setOrigin.accept(payment);
        payment.setPractice(posting.getLocation().getPractice());
        payment.setProfessional(pro);
        payment.setLocation(posting.getLocation());
        payment.setAmount(rate);
        return paymentDao.save(payment);
    }

    /**
     * Trigger that a job day of a temporary position should be paid for.
     * <br />
     * This does not imply synchronous or immediate payment.
     * @param jobDayId ID of the job day to pay for
     */
    @Transactional
    public void payTemporary(String jobDayId) {
        CheckIn checkIn = checkInDao.findOne(jobDayId);
        JobDay jobDay = checkIn.getJobDay();
        JobPosting posting = jobDay.getJobPosting();
        BigDecimal rate = rate(posting);

        createPayment(posting, rate, checkIn.getProfessional(), p -> p.setJobDay(jobDay));
    }

    /**
     * Trigger that a job interview for a permanent job should be paid for.
     * <br />
     * This does not imply synchronous or immediate payment.
     * @param interviewId ID of the job interview to pay for
     */
    @Transactional
    public void payInterview(String interviewId) {
        JobInterview interview = jobInterviewDao.findOne(interviewId);
        JobPosting posting = interview.getApplication().getPermanentJobPosting();
        BigDecimal rate = rate(posting);

        createPayment(posting, rate, interview.getApplication().getProfessional(), p -> p.setJobInterview(interview));
    }

    /**
     * Trigger that a permanent job contract should be paid for.
     * <br />
     * This does not imply synchronous or immediate payment.
     * @param jobApplicationId ID of the application to the permanent job to pay for
     */
    @Transactional
    public void payPermanent(String jobApplicationId) {
        PermanentJobPostingApplication application = permanentApplicationDao.findOne(jobApplicationId);
        PermanentJobPosting posting = application.getPermanentJobPosting();
        BigDecimal rate = calculateRate(posting);

        createPayment(posting, rate, application.getProfessional(), p -> p.setPermanentJobApplication(application));
    }

    @Transactional
    public PaymentPermanentInfo getPermanentPaymentInfo(String jobApplicationId) {
        PermanentJobPostingApplication application = permanentApplicationDao.findOne(jobApplicationId);
        PermanentJobPosting posting = application.getPermanentJobPosting();
        Professional professional = application.getProfessional();
        BigDecimal rate = calculateRate(posting);
        List<WorkSchedule> workSchedules = new ArrayList<>(posting.getWorkSchedules());
        workSchedules.sort(Comparator.comparing(ws -> ws.getWeekDay().getIndexNumber()));


        PaymentPermanentInfo info = new PaymentPermanentInfo();
        info.setProfessionalFirstName(professional.getContact().getName().getFirst());
        info.setProfessionalLastName(professional.getContact().getName().getLast());
        info.setSpecialities(professional.getSpecialties());
        info.setWorkingDaysPerWeek(workSchedules.stream().map(ws -> ws.getWeekDay().getName()).collect(joining(", ")));
        info.setHoursPerDay(8);
        info.setWeeksPerYear(52);
        info.setHourlyRate(rate(posting));
        info.setTotalFee(rate);

        return info;
    }

    private BigDecimal calculateRate(PermanentJobPosting posting) {
        Settings.Setting<Long> percentageFees = getPercentage(posting);
        int daysPerWeek = CollectionUtils.emptyIfNull(posting.getWorkSchedules()).size();
        return rate(posting)
                .multiply(new BigDecimal(8 /* hours per day*/))
                .multiply(new BigDecimal(52 /* weeks per year*/ * (daysPerWeek > 0 ? daysPerWeek : 5)))
                .multiply(new BigDecimal(systemSettings.getLong(percentageFees.getKey(), 20L)))
                .divide(new BigDecimal(100), 2, RoundingMode.CEILING);

    }

    private Settings.Setting<Long> getPercentage(PermanentJobPosting posting) {
        Category category = pickSpecialty(posting.getSubCategories()).getCategory();

        return Category.DENTISTS.equals(category.getId())
                ? PERCENT_FOR_HIRING_DENTISTS
                : PERCENT_FOR_HIRING_OTHERS;
    }
}
