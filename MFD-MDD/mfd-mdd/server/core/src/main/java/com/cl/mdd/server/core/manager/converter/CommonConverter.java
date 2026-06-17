package com.cl.mdd.server.core.manager.converter;

import com.cl.mdd.server.core.data.model.*;
import com.cl.mdd.server.core.data.model.certificates.BaseCertificateDetailsModel;
import com.cl.mdd.server.core.data.model.certificates.CertificateDetailsModel;
import com.cl.mdd.server.core.data.model.certificates.CertificateTypeModel;
import com.cl.mdd.server.core.data.model.certificates.OrthodonticsCertificateDetailsModel;
import com.cl.mdd.server.core.data.model.common.*;
import com.cl.mdd.server.core.data.model.query.JobInterviewTuple;
import com.cl.mdd.server.core.data.model.query.PermanentJobPostingApplicationSummaryTuple;
import com.cl.mdd.server.core.data.model.query.RequiredCertificate;
import com.cl.mdd.server.core.data.model.query.model.*;
import com.cl.mdd.server.core.data.model.settings.ProfessionalJobPreferenceModel;
import com.cl.mdd.server.core.data.persistent.access.common.*;
import com.cl.mdd.server.core.data.persistent.access.posting.PermanentJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.posting.TemporaryJobPostingApplicationDao;
import com.cl.mdd.server.core.data.persistent.access.practice.PracticeLocationDao;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.access.user.ProfessionalDao;
import com.cl.mdd.server.core.data.persistent.model.common.BayArea;
import com.cl.mdd.server.core.data.persistent.model.common.Language;
import com.cl.mdd.server.core.data.persistent.model.common.Speciality;
import com.cl.mdd.server.core.data.persistent.model.common.WeekDay;
import com.cl.mdd.server.core.data.persistent.model.common.embeddable.FullName;
import com.cl.mdd.server.core.data.persistent.model.contact.Address;
import com.cl.mdd.server.core.data.persistent.model.contact.Contact;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.PermanentJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterview;
import com.cl.mdd.server.core.data.persistent.model.posting.permanent.application.interview.JobInterviewOption;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.JobDay;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.TemporaryJobPosting;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.WorkSchedule;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.TemporaryJobPostingApplication;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.PracticeLocationProfessionalReview;
import com.cl.mdd.server.core.data.persistent.model.posting.temporary.application.review.ProfessionalPracticeLocationReview;
import com.cl.mdd.server.core.data.persistent.model.practice.Practice;
import com.cl.mdd.server.core.data.persistent.model.practice.PracticeLocation;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.data.persistent.model.specialty.CertificateType;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.PracticeOwner;
import com.cl.mdd.server.core.data.persistent.model.user.SystemUser;
import com.cl.mdd.server.core.data.persistent.model.user.User;
import com.cl.mdd.server.core.data.persistent.model.user.professional.*;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.Certificate;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.CertificateDetails;
import com.cl.mdd.server.core.data.persistent.model.user.professional.certification.OrthodonticsCertificateDetails;
import com.cl.mdd.server.core.data.persistent.model.user.professional.profile.*;
import com.cl.mdd.server.core.security.SecurityAccess;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cl.mdd.server.core.data.persistent.model.posting.JobPosting.ACTIVE;
import static com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication.ACCEPTED;
import static com.cl.mdd.server.core.data.persistent.model.posting.JobPostingApplication.BOOKED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Common converter.
 * <p/>
 * Performs conversion for the shared models.
 */
@Component
public class CommonConverter {

    @Autowired
    private SpecialityDao specialityDao;

    @Autowired
    private EducationDao educationDao;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private AcademicDegreeDao academicDegreeDao;

    @Autowired
    private WeekDayDao weekDayDao;

    @Autowired
    private PracticeLocationDao practiceLocationDao;

    @Autowired
    private ProfessionalDao professionalDao;

    @Autowired
    private TemporaryJobPostingApplicationDao temporaryJobPostingApplicationDao;

    @Autowired
    private PermanentJobPostingApplicationDao permanentJobPostingApplicationDao;

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityAccess securityAccess;


    public Contact toContact(ContactModel model) {
        Contact contact = null;
        if (model == null) {
            return contact;
        }

        contact = new Contact();
        contact.setName(toFullName(model.getName()));
        contact.setEmail(model.getEmail());
        contact.setPhone(model.getPhone());
        contact.setFax(model.getFax());
        contact.setAddress(toExistingAddress(model.getAddress()));

        return contact;
    }

    public ContactModel toContactModel(Contact contact) {
        ContactModel contactModel = null;
        if (contact == null) {
            return contactModel;
        }

        contactModel = new ContactModel();
        contactModel.setName(toFullNameModel(contact.getName()));
        contactModel.setEmail(contact.getEmail());
        contactModel.setPhone(contact.getPhone());
        contactModel.setFax(contact.getFax());
        contactModel.setAddress(toAddressModel(contact.getAddress()));

        return contactModel;
    }

    public FullName toFullName(FullNameModel model) {
        FullName fullName = null;
        if (model == null) {
            return fullName;
        }

        fullName = new FullName();
        fullName.setTitle(model.getTitle());
        fullName.setFirst(model.getFirst());
        fullName.setMiddle(model.getMiddle());
        fullName.setLast(model.getLast());

        return fullName;
    }

    public FullNameModel toFullNameModel(FullName fullName) {
        FullNameModel model = null;
        if (fullName == null) {
            return model;
        }

        model = new FullNameModel();
        model.setTitle(fullName.getTitle());
        model.setFirst(fullName.getFirst());
        model.setMiddle(fullName.getMiddle());
        model.setLast(fullName.getLast());

        return model;
    }

    public Address toExistingAddress(AddressModel model) {
        Address address = null;
        if (model == null) {
            return address;
        }

        address = new Address();
        return toExistingAddress(model, address);
    }

    private Address toExistingAddress(AddressModel model, Address address) {
        address.setCity(model.getCity());
        address.setCountry(model.getCountry());
        address.setState(model.getState());
        address.setStreet(model.getStreet());
        address.setZipCode(model.getZipCode());

        if (nonNull(model.getLatitude())) {
            address.setLatitude(model.getLatitude());
        }

        if (nonNull(model.getLongitude())) {
            address.setLongitude(model.getLongitude());
        }

        return address;
    }

    public AddressModel toAddressModel(Address address) {
        AddressModel model = null;
        if (address == null) {
            return model;
        }

        model = new AddressModel();
        model.setCity(address.getCity());
        model.setCountry(address.getCountry());
        model.setState(address.getState());
        model.setStreet(address.getStreet());
        model.setZipCode(address.getZipCode());
        model.setLatitude(address.getLatitude());
        model.setLongitude(address.getLongitude());

        return model;
    }

    /**
     * RegisterUser model to User conversion
     * <p/>
     *
     * @param model - source model
     * @param user  - target entity
     */
    public User toUser(RegisterUser model, User user) {
        user.setContact(toContact(model.getContact()));
        user.getContact().setEmail(model.getUsername()); // this two should be sync
        user.setUsername(model.getUsername()); // this two should be sync
        user.setPassword(passwordEncoder.encode(model.getPassword()));
        return user;
    }

    /**
     * User to User model conversion
     * <p/>
     *
     * @param entity - source entity
     * @param model  - target model
     */
    public <T extends UserModel> T toUserModel(User entity, T model) {
        model.setContact(toContactModel(entity.getContact()));
        model.setId(entity.getId());

        return model;
    }

    public Practice toPractice(BasePracticeModel model, Practice practice) {
        practice.setName(model.getName());
        practice.setPhone(model.getPhone());
        practice.setSecondEmail(model.getSecondEmail());
        practice.setAfterWorkPhone(model.getAfterWorkPhone());

        practice.setSoftwares(model.getSoftwares());

        Set<Speciality> specialities = emptyIfNull(model.getSpecialities()).stream()
                .map(this::toSpeciality)
                .collect(toSet());
        practice.setSpecialities(specialities);

        practice.setWebSite(model.getWebSite());
        practice.setBillingAddress(practice.getBillingAddress() == null ? toExistingAddress(model.getBillingAddress()) : toExistingAddress(model.getBillingAddress(), practice.getBillingAddress()));
        practice.setOfficeManagerName(model.getOfficeManagerName());

        return practice;
    }

    public PracticeModel toPracticeModel(Practice practice, PracticeModel practiceModel) {
        practiceModel.setId(practice.getId());
        practiceModel.setName(practice.getName());
        practiceModel.setPhone(practice.getPhone());
        practiceModel.setSecondEmail(practice.getSecondEmail());
        practiceModel.setAfterWorkPhone(practice.getAfterWorkPhone());
        practiceModel.setWebSite(practice.getWebSite());
        practiceModel.setOfficeManagerName(practice.getOfficeManagerName());
        practiceModel.setSoftwares(practice.getSoftwares());
        practiceModel.setSpecialities(emptyIfNull(practice.getSpecialities()).stream().map(Speciality::getId).collect(toSet()));
        if (isNotEmpty(practice.getLocations())) {
            practiceModel.setRating(practice.getLocations().stream().mapToDouble(PracticeLocation::getRating).average().orElse(0.00));
        }
        practiceModel.setBillingAddress(toAddressModel(practice.getBillingAddress()));

        return practiceModel;
    }

    public PracticeModel toPracticeModel(Practice practice) {
        if (practice == null) {
            return null;
        }
        PracticeModel practiceModel = new PracticeModel();

        return toPracticeModel(practice, practiceModel);
    }

    public Practice toPractice(RegisterPractice model) {
        Practice practice = new Practice();
        return toPractice(model, practice);
    }

    public PracticeOwner toPracticeOwner(RegisterPracticeOwner model) {
        PracticeOwner practiceOwner = new PracticeOwner();
        toUser(model, practiceOwner);

        return practiceOwner;
    }

    public Professional toProfessional(RegisterProfessional model) {
        Professional professional = new Professional();
        toUser(model, professional);

        return professional;
    }

    public SystemUser toSystemUser(RegisterSystemUser model) {
        SystemUser systemUser = new SystemUser();
        toUser(model, systemUser);

        return systemUser;
    }

    public SystemUserModel toSystemUserModel(SystemUser systemUser) {
        SystemUserModel model = new SystemUserModel();
        model.setId(systemUser.getId());
        model.setModified(systemUser.getModified());
        model.setState(systemUser.getStatus());
        model.setContact(toContactModel(systemUser.getContact()));

        return model;
    }

    public PracticeOwnerModel toPracticeOwnerModel(PracticeOwner practiceOwner) {
        PracticeOwnerModel model = new PracticeOwnerModel();
        toUserModel(practiceOwner, model);
        if (securityAccess.isCurrentSystemUser()) {
            model.setComments(practiceOwner.getComments());
        }
        return model;
    }

    public ProfessionalModel toProfessionalModel(Professional professional) {
        ProfessionalModel model = new ProfessionalModel();
        toUserModel(professional, model);
        model.setRating(professional.getRating());
        model.setNotificationsEnabled(professional.isNotificationsEnabled());
        model.setStatus(professional.getStatus());
        model.setNoShowCount(professional.getNoShow());
        model.setDenialsCount(professional.getDenials());
        model.setDateStarted(professional.getCreated());
        if (securityAccess.isCurrentSystemUser()) {
            model.setComments(professional.getComments());
        }
        return model;
    }

    public Speciality toSpeciality(String id) {
        if (id == null) {
            return null;
        }

        return specialityDao.findOne(id);
    }

    public SubCategory toSubcategory(SubcategoryModel model) {
        if (model == null) {
            return null;
        }
        SubCategory subCategory = new SubCategory();
        subCategory.setName(model.getName());
        subCategory.setId(model.getId());

        return subCategory;
    }

    public SpecialityModel toSpecialityModel(Speciality speciality) {

        if (speciality != null) {
            SpecialityModel model = new SpecialityModel();
            model.setName(speciality.getName());
            model.setId(speciality.getId());
            return model;
        }

        return null;
    }

    public SubcategoryModel toSubcategoryModel(SubCategory subCategory) {
        if (subCategory == null) {
            return null;
        }
        SubcategoryModel subcategoryModel = new SubcategoryModel();
        subcategoryModel.setName(subCategory.getName());
        subcategoryModel.setId(subCategory.getId());
        subcategoryModel.setCategory(toCategoryModel(subCategory.getCategory()));
        subcategoryModel.setCertificateTypes(toCertificateTypesModel(subCategory.getCertificateTypes()));
        return subcategoryModel;
    }

    private CertificateTypeModel toCertificateTypeModel(CertificateType certificateType) {
        return new CertificateTypeModel()
                .setId(certificateType.getId())
                .setOptional(certificateType.isOptional());
    }

    private Set<CertificateTypeModel> toCertificateTypesModel(Set<CertificateType> certificateTypes) {
        return emptyIfNull(certificateTypes).stream()
                .map(this::toCertificateTypeModel)
                .collect(toSet());
    }

    public CategoryModel toCategoryModel(Category category) {
        if (category != null) {
            CategoryModel model = new CategoryModel();
            model.setId(category.getId());
            model.setName(category.getName());
            return model;
        }
        return null;
    }

    public LanguageModel toLanguageModel(Language language) {
        if (language == null) {
            return null;
        }
        LanguageModel languageModel = new LanguageModel();
        languageModel.setName(language.getName());
        languageModel.setId(language.getId());

        return languageModel;
    }

    public PracticeLocation toPracticeLocation(AddPracticeLocation request) {
        PracticeLocation practiceLocation = new PracticeLocation();
        practiceLocation.setContact(toContact(request.getContact()));
        practiceLocation.setName(request.getName());
        practiceLocation.setWorkingHoursFrom(request.getWorkingHoursFrom());
        practiceLocation.setWorkingHoursTo(request.getWorkingHoursTo());
        practiceLocation.setTimeZone(ZoneId.of(request.getTimeZone()));
        return practiceLocation;
    }

    public PracticeLocation toPracticeLocation(UpdatePracticeLocation request) {
        PracticeLocation practiceLocation = new PracticeLocation();
        practiceLocation.setContact(toContact(request.getContact()));
        practiceLocation.setName(request.getName());
        practiceLocation.setWorkingHoursFrom(request.getWorkingHoursFrom());
        practiceLocation.setWorkingHoursTo(request.getWorkingHoursTo());
        practiceLocation.setTimeZone(ZoneId.of(request.getTimeZone()));
        practiceLocation.setId(request.getId());
        return practiceLocation;
    }

    public PracticeLocationModel toPracticeLocationModel(PracticeLocation entity) {
        PracticeLocationModel model = new PracticeLocationModel();
        model.setId(entity.getId());
        model.setContact(toContactModel(entity.getContact()));
        model.setWorkingHoursFrom(entity.getWorkingHoursFrom());
        model.setWorkingHoursTo(entity.getWorkingHoursTo());
        model.setName(entity.getName());
        model.setTimeZone(entity.getTimeZone().getId());
        model.setRating(entity.getRating());
        return model;
    }

    public ProfessionalJobPreference toProfessionalJobPreference(ProfessionalJobPreferenceModel model) {
        if (model != null) {
            ProfessionalJobPreference preference = new ProfessionalJobPreference();

            preference.setSalaryFrom(model.getSalaryFrom());
            preference.setSalaryTo(model.getSalaryTo());
            preference.setDesiredRatePerHour(model.getDesiredRatePerHour());
            preference.setCommutingRadius(model.getCommutingRadius());
            preference.setLookingForPermanentJob(model.getLookingForPermanentJob());
            preference.setLookingForTemporaryJob(model.getLookingForTemporaryJob());
            preference.setLookingForFullTimeJob(model.getLookingForFullTimeJob());
            preference.setLookingForPartTimeJob(model.getLookingForPartTimeJob());
            preference.setEveningWorkingHoursOk(model.getEveningWorkingHoursOk());
            preference.setAvailabilityDays(toWeekDays(model.getAvailabilityDays()));
            preference.setBayAreas(toBayArea(model.getBayAreas()));
            preference.setWillingToRelocate(model.getWillingToRelocate());

            return preference;
        }
        return null;
    }


    public ProfessionalJobPreferenceModel toProfessionalJobPreferenceModel(ProfessionalJobPreference model) {
        if (model != null) {
            ProfessionalJobPreferenceModel preference = new ProfessionalJobPreferenceModel();

            preference.setSalaryFrom(model.getSalaryFrom());
            preference.setSalaryTo(model.getSalaryTo());
            preference.setDesiredRatePerHour(model.getDesiredRatePerHour());
            preference.setCommutingRadius(model.getCommutingRadius());
            preference.setLookingForPermanentJob(model.isLookingForPermanentJob());
            preference.setLookingForTemporaryJob(model.isLookingForTemporaryJob());
            preference.setLookingForFullTimeJob(model.isLookingForFullTimeJob());
            preference.setLookingForPartTimeJob(model.isLookingForPartTimeJob());
            preference.setEveningWorkingHoursOk(model.isEveningWorkingHoursOk());
            preference.setAvailabilityDays(model.getAvailabilityDays().stream().map(WeekDay::getId).collect(toSet()));
            preference.setBayAreas(model.getBayAreas().stream().map(BayArea::getId).collect(toSet()));
            preference.setWillingToRelocate(model.isWillingToRelocate());

            return preference;
        }
        return null;
    }

    public ProfessionalSubcategoryModel toProfessionalSubCategoryModel(ProfessionalSubcategoryTuple from) {
        return new ProfessionalSubcategoryModel()
                .setId(from.getId())
                .setSubCategoryName(from.getSubCategoryName())
                .setCategoryName(from.getCategoryName())
                .setStatus(from.getStatus());
    }

    public RequiredCertificate toRequiredCertificate(RequiredCertificateTuple from) {
        return new RequiredCertificate()
                .setCertificateId(from.getCertificateId())
                .setStatus(from.getStatus())
                .setOptional(from.isOptional())
                .setType(from.getType());
    }

    public CertificateDetailsModel toCertificateDetailsModel(CertificateDetails certificateDetails) {
        if (nonNull(certificateDetails)) {
            if (certificateDetails instanceof OrthodonticsCertificateDetails) {
                OrthodonticsCertificateDetails certificate = (OrthodonticsCertificateDetails) certificateDetails;
                OrthodonticsCertificateDetailsModel model = toCertificateDetailsModel(certificate, new OrthodonticsCertificateDetailsModel());
                model.setEducation(certificate.getEducation());
                model.setSpeciality(certificate.getSpeciality());
                return model;
            }
            return toCertificateDetailsModel(certificateDetails, new BaseCertificateDetailsModel());
        }
        return null;
    }

    private <T extends CertificateDetailsModel> T toCertificateDetailsModel(CertificateDetails certificateDetails, T model) {
        model.setId(certificateDetails.getId())
                .setExpirationDate(certificateDetails.getExpirationDate())
                .setStatus(certificateDetails.getStatus())
                .setComment(certificateDetails.getComment())
                .setCertificate(toCertificateModel(certificateDetails.getCertificate()))
                .setLicenseNumber(certificateDetails.getLicenseNumber())
                .setCertificateType(toCertificateTypeModel(certificateDetails.getCertificateType()));
        return model;
    }

    private Set<BayArea> toBayArea(Set<String> areas) {
        if (CollectionUtils.isNotEmpty(areas)) {
            return areas.stream().map(area -> {
                BayArea bayArea = new BayArea();
                bayArea.setId(area);
                bayArea.setName(area);
                return bayArea;
            }).collect(toSet());
        }
        return null;
    }

    private Set<WeekDay> toWeekDays(Set<String> days) {
        if (CollectionUtils.isNotEmpty(days)) {
            return days.stream().map(day -> {
                WeekDay weekDay = new WeekDay();
                weekDay.setId(day);
                weekDay.setName(day);
                return weekDay;
            }).collect(toSet());
        }
        return null;
    }

    public ProfessionalProfile toProfessionalProfile(ProfessionalProfile target, ProfessionalProfileModel model) {
        target.setSkillSummary(model.getSkillSummary());
        target.setEducation(educationDao.getOne(model.getEducation()));
        target.setHighestAcademicDegree(academicDegreeDao.getOne(model.getHighestDegree()));
        target.setLanguages(model.getLanguages().stream().map(languageDao::getOne).collect(toSet()));

        List<ProfessionalWorkExperience> workExperiences = target.getWorkExperiences();
        if (workExperiences.isEmpty()) {
            //IF NO EXPERIENCES JUST PUSH ALL
            target.getWorkExperiences().addAll(model.getWorkExperiences()
                    .stream().map(this::toProfessionalWorkExperience).collect(Collectors.toList()));
        } else {
            //IF THERE ARE SOME EXPERIENCES UPDATE THEM
            Iterator<ProfessionalWorkExperience> targetIterator = workExperiences.iterator();
            Iterator<WorkExperienceModel> modelIterator = model.getWorkExperiences().iterator();
            while (targetIterator.hasNext() && modelIterator.hasNext()) {
                ProfessionalWorkExperience targetExperience = targetIterator.next();
                WorkExperienceModel modelExperience = modelIterator.next();
                targetExperience.setLeaveDate(modelExperience.getLeaveDate());
                targetExperience.setCompanyName(modelExperience.getCompanyName());
                targetExperience.setResponsibilities(modelExperience.getResponsibilities());
                targetExperience.setHireDate(modelExperience.getHireDate());
            }
            //AFTER PUSH THE REST
            while (modelIterator.hasNext()) {
                workExperiences.add(toProfessionalWorkExperience(modelIterator.next()));
            }
        }

        List<ProfessionalWorkReference> workReferences = target.getWorkReferences();
        //IF NO REFERENCES JUST PUSH ALL
        if (workReferences.isEmpty()) {
            target.getWorkReferences().addAll(model.getWorkReferences()
                    .stream().map(this::toProfessionalWorkReference).collect(Collectors.toList()));
        } else {
            //IF THERE ARE SOME REFERENCES UPDATE THEM
            Iterator<ProfessionalWorkReference> targetIterator = workReferences.iterator();
            Iterator<WorkReferenceModel> modelIterator = model.getWorkReferences().iterator();
            while (targetIterator.hasNext() && modelIterator.hasNext()) {
                ProfessionalWorkReference targetReference = targetIterator.next();
                WorkReferenceModel referenceModel = modelIterator.next();
                targetReference.setName(referenceModel.getName());
                targetReference.setEmail(referenceModel.getEmail());
                targetReference.setPhone(referenceModel.getPhone());
            }

            //AFTER PUSH THE REST
            while (modelIterator.hasNext()) {
                workReferences.add(toProfessionalWorkReference(modelIterator.next()));
            }
        }

        return target;
    }


    public ProfessionalProfileModel toProfessionalProfileModel(ProfessionalProfile professionalProfile) {
        if (professionalProfile == null) {
            return null;
        }
        ProfessionalProfileModel target = new ProfessionalProfileModel();
        target.setSkillSummary(professionalProfile.getSkillSummary());
        target.setEducation(professionalProfile.getEducation().getId());
        target.setHighestDegree(professionalProfile.getHighestAcademicDegree().getId());
        target.setLanguages(professionalProfile.getLanguages().stream().map(Language::getId).collect(toSet()));

        target.getWorkExperiences().addAll(professionalProfile.getWorkExperiences()
                .stream().map(this::toProfessionalWorkExperienceModel).collect(Collectors.toList()));
        target.getWorkReferences().addAll(professionalProfile.getWorkReferences()
                .stream().map(this::toProfessionalWorkReferenceModel).collect(Collectors.toList()));
        return target;
    }

    private WorkReferenceModel toProfessionalWorkReferenceModel(ProfessionalWorkReference professionalWorkReference) {
        WorkReferenceModel target = new WorkReferenceModel();
        target.setEmail(professionalWorkReference.getEmail());
        target.setPhone(professionalWorkReference.getPhone());
        target.setName(professionalWorkReference.getName());
        return target;
    }

    private WorkExperienceModel toProfessionalWorkExperienceModel(ProfessionalWorkExperience professionalWorkExperience) {
        WorkExperienceModel target = new WorkExperienceModel();
        target.setResponsibilities(professionalWorkExperience.getResponsibilities());
        target.setHireDate(professionalWorkExperience.getHireDate());
        target.setLeaveDate(professionalWorkExperience.getLeaveDate());
        target.setCompanyName(professionalWorkExperience.getCompanyName());
        return target;
    }

    private ProfessionalWorkReference toProfessionalWorkReference(WorkReferenceModel workReferenceModel) {
        ProfessionalWorkReference target = new ProfessionalWorkReference();
        target.setEmail(workReferenceModel.getEmail());
        target.setPhone(workReferenceModel.getPhone());
        target.setName(workReferenceModel.getName());
        return target;
    }

    private ProfessionalWorkExperience toProfessionalWorkExperience(WorkExperienceModel workExperienceModel) {
        ProfessionalWorkExperience target = new ProfessionalWorkExperience();
        target.setResponsibilities(workExperienceModel.getResponsibilities());
        target.setHireDate(workExperienceModel.getHireDate());
        target.setLeaveDate(workExperienceModel.getLeaveDate());
        target.setCompanyName(workExperienceModel.getCompanyName());
        return target;
    }

    public EducationModel toEducationModel(Education entity) {
        EducationModel target = new EducationModel();
        target.setId(entity.getId());
        target.setName(entity.getName());
        return target;
    }

    public AcademicDegreeModel toAcademicDegreeModel(AcademicDegree academicDegree) {
        AcademicDegreeModel academicDegreeModel = new AcademicDegreeModel();
        academicDegreeModel.setId(academicDegree.getId());
        academicDegreeModel.setName(academicDegree.getName());
        return academicDegreeModel;
    }


    public CertificateDetails toCertificateDetails(AddCertificateModel model) {
        return toCertificateDetails(model, new CertificateDetails());
    }

    public OrthodonticsCertificateDetails toOrthodonticsCertificateDetails(AddOrthodonticsCertificateModel model) {
        if (model != null) {
            OrthodonticsCertificateDetails certificateDetails = toCertificateDetails(model, new OrthodonticsCertificateDetails());

            return certificateDetails;
        }
        return null;
    }

    protected <T extends CertificateDetails> T toCertificateDetails(AddCertificateModel model, T certificateDetails) {
        if (model != null) {
            certificateDetails.setExpirationDate(model.getExpirationDate());
            if (nonNull(model.getFileName()) || nonNull(model.getFile()) || nonNull(model.getContentType())) {
                Certificate certificate = new Certificate();
                certificate.setName(model.getFileName());
                certificate.setContent(model.getFile());
                certificate.setContentType(model.getContentType());
                certificateDetails.setCertificate(certificate);
            }
            CertificateType certificateType = new CertificateType();
            certificateType.setId(model.getType());
            certificateDetails.setCertificateType(certificateType);
            certificateDetails.setLicenseNumber(model.getLicenseNumber());
            return certificateDetails;
        }
        return null;
    }

    public CertificateModel toCertificateModel(Certificate certificate) {
        if (nonNull(certificate)) {
            CertificateModel model = new CertificateModel();
            model.setId(certificate.getId());
            model.setName(certificate.getName());
            model.setContentType(certificate.getContentType());
            return model;
        }
        return null;
    }


    public TemporaryJobPosting toNewActiveTemporaryJobPosting(PublishTemporaryJobPosting publishTemporaryJobPosting) {
        TemporaryJobPosting target = new TemporaryJobPosting();
        toTemporaryJobPosting(publishTemporaryJobPosting, target);
        return target;
    }

    public void toTemporaryJobPosting(PublishTemporaryJobPosting temporaryJobPosting, TemporaryJobPosting target) {
        target.setStatus(ACTIVE);
        target.setStartDate(temporaryJobPosting.getStartDate());
        target.setEndDate(temporaryJobPosting.getEndDate());
        target.setLocation(practiceLocationDao.getOne(temporaryJobPosting.getPracticeLocationId()));
        target.setName(temporaryJobPosting.getName());
        target.getSubCategories().clear();
        target.getLanguages().clear();
        target.getSubCategories().addAll(temporaryJobPosting.getRequiredSubcategories().stream().map(subCategoryDao::getOne).collect(toSet()));
        target.getLanguages().addAll(temporaryJobPosting.getRequiredLanguages().stream().map(languageDao::getOne).collect(toSet()));
        target.setComment(temporaryJobPosting.getComment());
        if (nonNull(temporaryJobPosting.getPreferredCandidateId())) {
            target.setPreferredProfessional(professionalDao.getOne(temporaryJobPosting.getPreferredCandidateId()));
        }
    }

    public PermanentJobPosting toNewActivePermanentJobPosting(PublishSimplePermanentJobPosting simplePermanentJobPosting) {
        PermanentJobPosting target = new PermanentJobPosting();
        toPermanentJobPosting(simplePermanentJobPosting, target);
        return target;
    }

    public void toPermanentJobPosting(PublishSimplePermanentJobPosting simplePermanentJobPosting, PermanentJobPosting target) {
        target.setStatus(ACTIVE);
        target.setStartDate(simplePermanentJobPosting.getStartDate());
        target.setLocation(practiceLocationDao.getOne(simplePermanentJobPosting.getPracticeLocationId()));
        target.setName(simplePermanentJobPosting.getName());
        target.setSubCategories(simplePermanentJobPosting.getRequiredSubcategories().stream().map(subCategoryDao::getOne).collect(toSet()));
        target.setLanguages(simplePermanentJobPosting.getRequiredLanguages().stream().map(languageDao::getOne).collect(toSet()));
        target.setComment(simplePermanentJobPosting.getComment());
        target.getWorkSchedules().clear();
        target.getWorkSchedules().addAll(simplePermanentJobPosting.getWorkSchedules().stream().map(this::toWorkSchedule).collect(toSet()));
        if (nonNull(simplePermanentJobPosting.getPreferredCandidateId())) {
            target.setPreferredProfessional(professionalDao.getOne(simplePermanentJobPosting.getPreferredCandidateId()));
        }
    }

    public JobDay toJobDay(JobDayModel jobDayModel, TemporaryJobPosting posting) {
        JobDay jobDay = new JobDay();
        jobDay.setJobPosting(posting);
        jobDay.setDate(jobDayModel.getDate());
        jobDay.setStartTime(jobDayModel.getStartTime());
        jobDay.setEndTime(jobDayModel.getEndTime());
        jobDay.setExcluded(jobDayModel.isExcluded());
        return jobDay;
    }

    public JobDayModel toJobDay(JobDay jobDay) {
        JobDayModel jobDayModel = new JobDayModel();
        jobDayModel.setDate(jobDay.getDate());
        jobDayModel.setStartTime(jobDay.getStartTime());
        jobDayModel.setEndTime(jobDay.getEndTime());
        jobDayModel.setExcluded(jobDay.isExcluded());
        return jobDayModel;
    }

    public ZonedJobDayModel toZonedJobDay(JobDay jobDay) {
        ZonedJobDayModel jobDayModel = new ZonedJobDayModel();
        jobDayModel.setDate(jobDay.getDate());
        jobDayModel.setStartTime(jobDay.getZonedStartDateTime());
        jobDayModel.setEndTime(jobDay.getZonedEndDateTime());
        jobDayModel.setExcluded(jobDay.isExcluded());
        return jobDayModel;
    }

    public JobPosting toJobPosting(com.cl.mdd.server.core.data.persistent.model.posting.JobPosting jobPosting) {
        if (jobPosting instanceof TemporaryJobPosting) {
            return toJobPosting((TemporaryJobPosting) jobPosting);
        }

        if (jobPosting instanceof PermanentJobPosting) {
            return toJobPosting((PermanentJobPosting) jobPosting);
        }
        return null;
    }

    private JobPosting toJobPosting(TemporaryJobPosting jobPosting) {
        String jobDayStrategy = jobPosting.getJobDayStrategy();
        switch (jobDayStrategy) {
            case TemporaryJobPosting.JOB_DAY_STRATEGY_SIMPLE:
                return toSimpleJobTemporaryPosting(jobPosting);
            case TemporaryJobPosting.JOB_DAY_STRATEGY_WEEKLY:
                return toWeeklyJobTemporaryPosting(jobPosting);
            case TemporaryJobPosting.JOB_DAY_STRATEGY_COMPLEX:
                return toComplexJobTemporaryPosting(jobPosting);
            default:
                throw new IllegalArgumentException(jobDayStrategy);
        }
    }

    private JobPosting toJobPosting(PermanentJobPosting jobPosting) {
        return toSimpleJobPermanentPosting(jobPosting);
    }

    private JobPosting toSimpleJobTemporaryPosting(TemporaryJobPosting jobPosting) {
        ViewSimpleTemporaryJobPosting simpleTemporaryJobPosting = new ViewSimpleTemporaryJobPosting();
        simpleTemporaryJobPosting.setId(jobPosting.getId());
        simpleTemporaryJobPosting.setStartDate(jobPosting.getStartDate());
        simpleTemporaryJobPosting.setEndDate(jobPosting.getEndDate());
        simpleTemporaryJobPosting.setStartTime(jobPosting.getStartTime());
        simpleTemporaryJobPosting.setEndTime(jobPosting.getEndTime());
        simpleTemporaryJobPosting.setPracticeLocationId(jobPosting.getLocation().getId());
        simpleTemporaryJobPosting.setPracticeLocationAddressCity(jobPosting.getLocation().getContact().getAddress().getCity());
        simpleTemporaryJobPosting.setName(jobPosting.getName());
        simpleTemporaryJobPosting.setRequiredSubcategories(jobPosting.getSubCategories().stream().map(SubCategory::getId).collect(toSet()));
        simpleTemporaryJobPosting.setRequiredLanguages(jobPosting.getLanguages().stream().map(Language::getId).collect(toSet()));
        simpleTemporaryJobPosting.setComment(jobPosting.getComment());
        simpleTemporaryJobPosting.setZonedJobDays(toJobDays(jobPosting));
        return simpleTemporaryJobPosting;
    }

    private JobPosting toWeeklyJobTemporaryPosting(TemporaryJobPosting jobPosting) {
        ViewWeeklyTemporaryJobPosting weeklyTemporaryJobPosting = new ViewWeeklyTemporaryJobPosting();
        weeklyTemporaryJobPosting.setId(jobPosting.getId());
        weeklyTemporaryJobPosting.setStartDate(jobPosting.getStartDate());
        weeklyTemporaryJobPosting.setEndDate(jobPosting.getEndDate());
        weeklyTemporaryJobPosting.setPracticeLocationId(jobPosting.getLocation().getId());
        weeklyTemporaryJobPosting.setPracticeLocationAddressCity(jobPosting.getLocation().getContact().getAddress().getCity());
        weeklyTemporaryJobPosting.setName(jobPosting.getName());
        weeklyTemporaryJobPosting.setRequiredSubcategories(jobPosting.getSubCategories().stream().map(SubCategory::getId).collect(toSet()));
        weeklyTemporaryJobPosting.setRequiredLanguages(jobPosting.getLanguages().stream().map(Language::getId).collect(toSet()));
        weeklyTemporaryJobPosting.setComment(jobPosting.getComment());
        weeklyTemporaryJobPosting.setWorkSchedules(jobPosting.getWorkSchedules().stream().map(this::toWorkScheduleModel).collect(Collectors.toList()));
        weeklyTemporaryJobPosting.setZonedJobDays(toJobDays(jobPosting));
        return weeklyTemporaryJobPosting;
    }

    private List<ZonedJobDayModel> toJobDays(TemporaryJobPosting jobPosting) {
        Set<LocalDate> notAvailable = jobPosting.getApplications().stream()
                .filter(application -> application.getStatus().equals(ACCEPTED) || application.getStatus().equals(BOOKED))
                .flatMap(temporaryJobPostingApplication -> temporaryJobPostingApplication.getJobDays().stream())
                .map(JobDay::getDate)
                .collect(toSet());

        return jobPosting.getJobDays().stream().map(jobDay -> {
                    ZonedJobDayModel zonedJobDayModel = toZonedJobDay(jobDay);
                    if (!zonedJobDayModel.isExcluded()) {
                        zonedJobDayModel.setExcluded(ZonedDateTime.now().isAfter(jobDay.getZonedStartDateTime()) ||
                                notAvailable.contains(zonedJobDayModel.getDate()));
                    }
                    return zonedJobDayModel;
                }

        ).collect(Collectors.toList());
    }

    private WorkScheduleModel toWorkScheduleModel(WorkSchedule workSchedule) {
        WorkScheduleModel to = new WorkScheduleModel();
        to.setWeekDay(workSchedule.getWeekDay().getId());
        to.setStartTime(workSchedule.getStartTime());
        to.setEndTime(workSchedule.getEndTime());
        return to;
    }

    public WorkSchedule toWorkSchedule(WorkScheduleModel workSchedule) {
        WorkSchedule to = new WorkSchedule();
        to.setWeekDay(weekDayDao.getOne(workSchedule.getWeekDay()));
        to.setStartTime(workSchedule.getStartTime());
        to.setEndTime(workSchedule.getEndTime());
        return to;
    }

    private JobPosting toComplexJobTemporaryPosting(TemporaryJobPosting jobPosting) {
        ViewComplexTemporaryJobPosting complexTemporaryJobPosting = new ViewComplexTemporaryJobPosting();
        complexTemporaryJobPosting.setId(jobPosting.getId());
        complexTemporaryJobPosting.setStartDate(jobPosting.getStartDate());
        complexTemporaryJobPosting.setEndDate(jobPosting.getEndDate());
        complexTemporaryJobPosting.setPracticeLocationId(jobPosting.getLocation().getId());
        complexTemporaryJobPosting.setPracticeLocationAddressCity(jobPosting.getLocation().getContact().getAddress().getCity());
        complexTemporaryJobPosting.setName(jobPosting.getName());
        complexTemporaryJobPosting.setRequiredSubcategories(jobPosting.getSubCategories().stream().map(SubCategory::getId).collect(toSet()));
        complexTemporaryJobPosting.setRequiredLanguages(jobPosting.getLanguages().stream().map(Language::getId).collect(toSet()));
        complexTemporaryJobPosting.setComment(jobPosting.getComment());
        complexTemporaryJobPosting.setJobDays(jobPosting.getJobDays().stream().map(this::toJobDay).collect(Collectors.toList()));
        complexTemporaryJobPosting.setZonedJobDays(toJobDays(jobPosting));
        return complexTemporaryJobPosting;
    }

    private JobPosting toSimpleJobPermanentPosting(PermanentJobPosting jobPosting) {
        SimplePermanentJobPosting simpleTemporaryJobPosting = new SimplePermanentJobPosting();
        simpleTemporaryJobPosting.setId(jobPosting.getId());
        simpleTemporaryJobPosting.setStartDate(jobPosting.getStartDate());
        simpleTemporaryJobPosting.setPracticeLocationId(jobPosting.getLocation().getId());
        simpleTemporaryJobPosting.setName(jobPosting.getName());
        simpleTemporaryJobPosting.setRequiredSubcategories(jobPosting.getSubCategories().stream().map(SubCategory::getId).collect(toSet()));
        simpleTemporaryJobPosting.setRequiredLanguages(jobPosting.getLanguages().stream().map(Language::getId).collect(toSet()));
        simpleTemporaryJobPosting.setComment(jobPosting.getComment());
        simpleTemporaryJobPosting.setWorkSchedules(jobPosting.getWorkSchedules().stream().map(this::toWorkScheduleModel).collect(Collectors.toList()));
        return simpleTemporaryJobPosting;
    }

    public ProfessionalTemporaryJobPosting toProfessionalTemporaryJobPosting(ProfessionalTemporaryJobPostingTuple from) {
        return new ProfessionalTemporaryJobPosting().setId(from.getId())
                .setApplicationId(from.getApplicationId())
                .setName(from.getName())
                .setApplicationStatus(from.getApplicationStatus())
                .setJobDayId(from.getJobDayId())
                .setStartDate(from.getStartDate())
                .setEndDate(from.getEndDate())
                .setStartTime(from.getStartTime())
                .setEndTime(from.getEndTime())
                .setDistance(from.getDistance())
                .setPracticeName(from.getPracticeName())
                .setPracticeLocationId(from.getPracticeLocationId())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setPracticeLocationAddressCity(from.getPracticeLocationAddressCity())
                .setPostedDate(from.getPostedDate())
                .setAlerted(from.isAlerted());
    }

    public ProfessionalPermanentJobPosting toProfessionalPermanentJobPosting(ProfessionalPermanentJobPostingTuple from) {
        return new ProfessionalPermanentJobPosting().setId(from.getId())
                .setName(from.getName())
                .setApplicationId(from.getApplicationId())
                .setApplicationStatus(from.getApplicationStatus())
                .setInterviewId(from.getInterviewId())
                .setStartDate(from.getStartDate())
                .setDistance(from.getDistance())
                .setPracticeName(from.getPracticeName())
                .setPracticeLocationId(from.getPracticeLocationId())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setPracticeLocationAddressCity(from.getPracticeLocationAddressCity())
                .setPostedDate(from.getPostedDate());
    }


    public PracticeOwnerPermanentJobPosting toPracticeOwnerPermanentJobPosting(PracticeOwnerPermanentJobPostingTuple from) {
        return new PracticeOwnerPermanentJobPosting().setId(from.getId())
                .setName(from.getName())
                .setStatus(from.getStatus())
                .setStartDate(from.getStartDate())
                .setApplicants(from.getApplicants())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setPostedDate(from.getPostedDate());
    }

    public PracticeOwnerTemporaryJobPosting toPracticeOwnerTemporaryJobPosting(PracticeOwnerTemporaryJobPostingTuple from) {
        return new PracticeOwnerTemporaryJobPosting().setId(from.getId())
                .setName(from.getName())
                .setStatus(from.getStatus())
                .setStartDate(from.getStartDate())
                .setEndDate(from.getEndDate())
                .setStartTime(from.getStartTime())
                .setEndTime(from.getEndTime())
                .setApplicants(from.getApplicants())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setPostedDate(from.getPostedDate());
    }

    public SystemUserTemporaryJobPosting toSystemUserTemporaryJobPosting(SystemUserTemporaryJobPostingTuple from) {
        return new SystemUserTemporaryJobPosting().setId(from.getId())
                .setName(from.getName())
                .setStatus(from.getStatus())
                .setStartDate(from.getStartDate())
                .setEndDate(from.getEndDate())
                .setStartTime(from.getStartTime())
                .setEndTime(from.getEndTime())
                .setApplicants(from.getApplicants())
                .setPracticeOwnerFirstName(from.getPracticeOwnerFirstName())
                .setPracticeOwnerLastName(from.getPracticeOwnerLastName())
                .setPracticeName(from.getPracticeName())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setPostedDate(from.getPostedDate());
    }

    public SystemUserPermanentJobPosting toSystemUserPermanentJobPosting(SystemUserPermanentJobPostingTuple from) {
        return new SystemUserPermanentJobPosting().setId(from.getId())
                .setName(from.getName())
                .setStatus(from.getStatus())
                .setStartDate(from.getStartDate())
                .setApplicants(from.getApplicants())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setPracticeOwnerFirstName(from.getPracticeOwnerFirstName())
                .setPracticeOwnerLastName(from.getPracticeOwnerLastName())
                .setPracticeName(from.getPracticeName())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setPostedDate(from.getPostedDate());
    }

    public NoShowModel toNoShowModel(NoShow noShow) {
        if (Objects.isNull(noShow)) {
            return null;
        }
        JobDay jobDay = noShow.getJobDay();
        com.cl.mdd.server.core.data.persistent.model.posting.JobPosting jobPosting = jobDay.getJobPosting();
        PracticeOwner owner = jobPosting.getLocation().getPractice().getOwner();
        FullName name = owner.getContact().getName();

        NoShowModel noShowModel = new NoShowModel();
        noShowModel.setId(noShow.getId())
                .setFirstName(name.getFirst())
                .setLastName(name.getLast())
                .setOffice(jobPosting.getLocation().getName())
                .setPosting(jobPosting.getName())
                .setDate(jobDay.getDate())
                .setStatus(noShow.getStatus())
                .setComments(noShow.getComments());

        return noShowModel;
    }

    public Attendance toAttendance(AttendanceTuple attendanceTuple) {
        return new Attendance()
                .setJobDayId(attendanceTuple.getJobDayId())
                .setAttendanceStartDateTime(attendanceTuple.getAttendanceStartDateTime())
                .setAttendanceEndDateTime(attendanceTuple.getAttendanceEndDateTime())
                .setDate(attendanceTuple.getDate())
                .setStartTime(attendanceTuple.getStartTime())
                .setEndTime(attendanceTuple.getEndTime())
                .setProfessionalId(attendanceTuple.getProfessionalId())
                .setProfessionalFirstName(attendanceTuple.getProfessionalFirstName())
                .setProfessionalLastName(attendanceTuple.getProfessionalLastName())
                .setJobPostingName(attendanceTuple.getJobPostingName())
                .setJobDayStatus(attendanceTuple.getJobDayStatus())
                .setPracticeLocationName(attendanceTuple.getPracticeLocationName());
    }

    public PracticeLocationProfessionalReview toPracticeLocationProfessionalReview(LocationToProfessionalReview review) {
        PracticeLocationProfessionalReview entity = new PracticeLocationProfessionalReview();
        toExistingPracticeLocationProfessionalReview(review, entity);
        return entity;
    }

    public ProfessionalPracticeLocationReview toProfessionalPracticeLocationReview(ProfessionalToLocationReview review) {
        ProfessionalPracticeLocationReview entity = new ProfessionalPracticeLocationReview();
        toExistingProfessionalPracticeLocationReview(review, entity);
        return entity;
    }

    public void toExistingPracticeLocationProfessionalReview(LocationToProfessionalReview review, PracticeLocationProfessionalReview entity) {
        if (entity != null) {
            entity.setProfessionalismRate(review.getProfessionalismRate());
            entity.setCommunicationRate(review.getCommunicationRate());
            entity.setWorkQualityRate(review.getWorkQualityRate());
            entity.setPunctualityRate(review.getPunctualityRate());
            entity.setAppearanceRate(review.getAppearanceRate());
            entity.setWouldHire(review.isWouldHire());
            entity.setComment(review.getComment());
            entity.setApplication(temporaryJobPostingApplicationDao.getOne(review.getApplicationId()));
        }
    }

    public void toExistingProfessionalPracticeLocationReview(ProfessionalToLocationReview review, ProfessionalPracticeLocationReview entity) {
        if (entity != null) {
            entity.setRate(review.getRate());
            entity.setWouldWorkPermanently(review.isWouldWorkPermanently());
            entity.setComment(review.getComment());
            entity.setApplication(temporaryJobPostingApplicationDao.getOne(review.getApplicationId()));
        }
    }

    public LocationToProfessionalReview toProfessionalPracticeLocationReviewModel(PracticeLocationProfessionalReview review) {
        if (isNull(review)) {
            return null;
        }
        LocationToProfessionalReview model = new LocationToProfessionalReview();
        model.setProfessionalismRate(review.getProfessionalismRate());
        model.setCommunicationRate(review.getCommunicationRate());
        model.setWorkQualityRate(review.getWorkQualityRate());
        model.setPunctualityRate(review.getPunctualityRate());
        model.setAppearanceRate(review.getAppearanceRate());
        model.setWouldHire(review.isWouldHire());
        model.setComment(review.getComment());
        model.setApplicationId(review.getApplication().getId());
        return model;
    }

    public ProfessionalToLocationReview toProfessionalPracticeLocationReviewModel(ProfessionalPracticeLocationReview review) {
        if (isNull(review)) {
            return null;
        }
        ProfessionalToLocationReview model = new ProfessionalToLocationReview();
        model.setRate(review.getRate());
        model.setWouldWorkPermanently(review.isWouldWorkPermanently());
        model.setComment(review.getComment());
        model.setApplicationId(review.getApplication().getId());
        return model;
    }

    public LocationToProfessionalReviewSummary toLocationToProfessionalReviewSummary(LocationToProfessionalReviewSummaryTuple from) {
        return new LocationToProfessionalReviewSummary().setId(from.getId())
                .setJobPostingName(from.getJobPostingName())
                .setPracticeOwnerFirstName(from.getPracticeOwnerFirstName())
                .setPracticeOwnerLastName(from.getPracticeOwnerLastName())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setStartDate(from.getStartDate())
                .setEndDate(from.getEndDate())
                .setProfessionalismRate(from.getProfessionalismRate())
                .setCommunicationRate(from.getCommunicationRate())
                .setWorkQualityRate(from.getWorkQualityRate())
                .setPunctualityRate(from.getPunctualityRate())
                .setAppearanceRate(from.getAppearanceRate())
                .setTotalScore(from.getTotalScore())
                .setFeedbackDate(from.getFeedbackDate())
                .setComment(from.getComment());
    }


    public ProfessionalToLocationReviewSummary toProfessionalToLocationReviewSummary(ProfessionalToLocationReviewSummaryTuple from) {
        return new ProfessionalToLocationReviewSummary()
                .setId(from.getId())
                .setJobPostingName(from.getJobPostingName())
                .setProfessionalId(from.getProfessionalId())
                .setProfessionalFirstName(from.getProfessionalFirstName())
                .setProfessionalLastName(from.getProfessionalLastName())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setStartDate(from.getStartDate())
                .setEndDate(from.getEndDate())
                .setRate(from.getRate())
                .setWouldWorkPermanently(from.isWouldWorkPermanently())
                .setBlackListed(from.isBlackListed())
                .setComment(from.getComment())
                .setFeedbackDate(from.getFeedbackDate());
    }

    public PreviouslyHiredProfessional toPreviouslyHiredProfessional(PreviouslyHiredProfessionalTuple from) {
        return new PreviouslyHiredProfessional().setId(from.getId())
                .setFirstName(from.getFirstName())
                .setLastName(from.getLastName())
                .setLastEmploymentDate(from.getLastEmploymentDate())
                .setBlackListed(from.isBlackListed())
                .setTotalRating(from.getTotalRating());
    }

    public ProfessionalPreviousJobForEmployer toProfessionalPreviousJobForEmployer(ProfessionalPreviousJobForEmployerTuple from) {
        return new ProfessionalPreviousJobForEmployer()
                .setJobPostingApplicationId(from.getJobPostingApplicationId())
                .setJobPostingName(from.getJobPostingName())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setStartDate(from.getStartDate())
                .setEndDate(from.getEndDate())
                .setHasReview(from.isHasReview());
    }

    public ProfessionalPreviousJobForEmployee toProfessionalPreviousJobForEmployee(ProfessionalPreviousJobForEmployeeTuple from) {
        return new ProfessionalPreviousJobForEmployee()
                .setJobPostingApplicationId(from.getJobPostingApplicationId())
                .setJobPostingId(from.getJobPostingId())
                .setJobPostingName(from.getJobPostingName())
                .setStartDate(from.getStartDate())
                .setEndDate(from.getEndDate())
                .setHasReview(from.isHasReview())
                .setPracticeName(from.getPracticeName())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setDistance(from.getDistance())
                .setLocationRating(from.getLocationRating());
    }

    public PermanentJobPostingApplicationSummary toPermanentJobPostingApplicationSummary(PermanentJobPostingApplicationSummaryTuple from) {
        return new PermanentJobPostingApplicationSummary().setId(from.getId())
                .setProfessionalId(from.getProfessionalId())
                .setFirstName(from.getFirstName())
                .setLastName(from.getLastName())
                .setSpecialty(from.getSpecialty())
                .setRph(from.getRph())
                .setRating(from.getRating())
                .setInterviewId(from.getInterviewId())
                .setInterviewStatus(from.getInterviewStatus())
                .setBookingStatus(from.getBookingStatus())
                .setCurrentState(from.isCurrentState());
    }

    public JobInterview toJobInterview(ScheduleJobInterview scheduleJobInterview) {
        JobInterview jobInterview = new JobInterview();
        jobInterview.setStatus(JobInterview.INVITED);
        jobInterview.setType(scheduleJobInterview.isWorking() ? JobInterview.WORKING : JobInterview.PERSONAL);
        jobInterview.setApplication(permanentJobPostingApplicationDao.getOne(scheduleJobInterview.getApplicationId()));
        jobInterview.setComments(scheduleJobInterview.getComments());
        jobInterview.setJobInterviewOptions(scheduleJobInterview.getOptions().stream().map(toJobInterviewOption(jobInterview)).collect(Collectors.toList()));
        return jobInterview;
    }

    private Function<JobInterviewScheduleOption, JobInterviewOption> toJobInterviewOption(JobInterview jobInterview) {
        return jobInterviewScheduleOption -> {
            JobInterviewOption jobInterviewOption = new JobInterviewOption();
            jobInterviewOption.setDate(jobInterviewScheduleOption.getDate());
            jobInterviewOption.setTime(jobInterviewScheduleOption.getTime());
            jobInterviewOption.setJobInterview(jobInterview);
            return jobInterviewOption;
        };
    }

    public ScheduledJobInterview toJobInterviewModel(JobInterview db) {
        if (isNull(db)) {
            return null;
        }
        ScheduledJobInterview scheduledJobInterview = new ScheduledJobInterview();
        scheduledJobInterview.setId(db.getId())
                .setComments(db.getComments())
                .setWorking(JobInterview.WORKING.equals(db.getType()))
                .setAcceptedOption(toScheduledOption().apply(db.getAcceptedOption()))
                .setOptions(db.getJobInterviewOptions().stream().map(toScheduledOption()).collect(Collectors.toList()));
        return scheduledJobInterview;
    }

    private Function<JobInterviewOption, JobInterviewScheduledOption> toScheduledOption() {
        return jobInterviewOption -> {
            if (jobInterviewOption == null) {
                return null;
            }
            JobInterviewScheduledOption option = new JobInterviewScheduledOption();
            option.setId(jobInterviewOption.getId());
            option.setDateTime(jobInterviewOption.getZonedStartDateTime());
            return option;
        };
    }

    public ViewJobInterview toViewJobInterview(JobInterviewTuple from) {
        return new ViewJobInterview()
                .setId(from.getId())
                .setJobPostingName(from.getJobPostingName())
                .setPracticeOwnerFirstName(from.getPracticeOwnerFirstName())
                .setPracticeOwnerLastName(from.getPracticeOwnerLastName())
                .setPracticeName(from.getPracticeName())
                .setPracticeLocationName(from.getPracticeLocationName())
                .setProfessionalFirstName(from.getProfessionalFirstName())
                .setProfessionalLastName(from.getProfessionalLastName())
                .setStatus(from.getStatus())
                .setDate(from.getDate())
                .setTime(from.getTime())
                .setType(from.getType())
                .setNumberOfInterview(from.getNumberOfInterview());
    }

    public BlackListedProfessionalDetails toBlackListedProfessionalDetails(BlackListedProfessional blackListedProfessional) {
        Practice practice = blackListedProfessional.getPractice();
        FullName professionalName = blackListedProfessional.getProfessional().getContact().getName();
        FullName practiceOwnerName = practice.getOwner().getContact().getName();
        return new BlackListedProfessionalDetails()
                .setPracticeId(practice.getId())
                .setPracticeName(practice.getName())
                .setPracticeOwnerFirstName(practiceOwnerName.getFirst())
                .setPracticeOwnerLastName(practiceOwnerName.getLast())
                .setProfessionalFirstName(professionalName.getFirst())
                .setProfessionalLastName(professionalName.getLast())
                .setBlackListDate(blackListedProfessional.getDate());
    }

    public BlackListedLocationDetails toBlackListedLocationDetails(BlackListedPracticeLocation blackListedPracticeLocation) {
        PracticeLocation location = blackListedPracticeLocation.getLocation();
        Practice practice = location.getPractice();
        FullName professionalName = blackListedPracticeLocation.getProfessional().getContact().getName();
        FullName practiceOwnerName = practice.getOwner().getContact().getName();
        return new BlackListedLocationDetails()
                .setPracticeId(practice.getId())
                .setLocationId(location.getId())
                .setPracticeName(practice.getName())
                .setPracticeLocationName(location.getName())
                .setPracticeOwnerFirstName(practiceOwnerName.getFirst())
                .setPracticeOwnerLastName(practiceOwnerName.getLast())
                .setProfessionalFirstName(professionalName.getFirst())
                .setProfessionalLastName(professionalName.getLast())
                .setBlackListDate(blackListedPracticeLocation.getBlackListedDate())
                .setUnblackListDate(blackListedPracticeLocation.getUnblackListedDate());
    }

    public BlackListedLocationSummary toBlackListedLocationSummary(BlackListedPracticeLocation blackListedPracticeLocation) {
        PracticeLocation location = blackListedPracticeLocation.getLocation();
        Practice practice = location.getPractice();
        FullName professionalName = blackListedPracticeLocation.getProfessional().getContact().getName();
        return new BlackListedLocationSummary()
                .setPracticeId(practice.getId())
                .setLocationId(location.getId())
                .setPracticeName(practice.getName())
                .setPracticeLocationId(location.getId())
                .setPracticeLocationName(location.getName())
                .setProfessionalFirstName(professionalName.getFirst())
                .setProfessionalLastName(professionalName.getLast())
                .setBlackListDate(blackListedPracticeLocation.getBlackListedDate())
                .setUnblackListDate(blackListedPracticeLocation.getUnblackListedDate());
    }

    public DirectBookingCandidate toDirectBookingCandidate(DirectBookingCandidateTuple from) {
        return new DirectBookingCandidate()
                .setId(from.getId())
                .setFirstName(from.getFirstName())
                .setLastName(from.getLastName())
                .setRatePerHour(from.getRatePerHour())
                .setTotalRating(from.getTotalRating());
    }

    public ViewTemporaryJobPostingApplication toJobPostingApplicationModel(TemporaryJobPostingApplication db) {
        if (isNull(db)) {
            return null;
        }
        return new ViewTemporaryJobPostingApplication().setId(db.getId())
                .setJobDays(db.getJobDays().stream().map(this::toJobDay).collect(Collectors.toList()))
                .setZonedJobDays(db.getJobDays().stream().map(this::toZonedJobDay).collect(Collectors.toList()));
    }
}
