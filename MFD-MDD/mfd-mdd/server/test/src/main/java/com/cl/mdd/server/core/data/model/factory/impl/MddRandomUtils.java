package com.cl.mdd.server.core.data.model.factory.impl;

import com.cl.mdd.server.core.data.model.WorkScheduleModel;
import com.cl.mdd.server.core.data.persistent.access.common.*;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.model.common.BayArea;
import com.cl.mdd.server.core.data.persistent.model.common.Language;
import com.cl.mdd.server.core.data.persistent.model.common.WeekDay;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.profile.AcademicDegree;
import com.cl.mdd.server.core.data.persistent.model.user.professional.profile.Education;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalTime.of;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@Component
public class MddRandomUtils {

    public static final String GENERAL_DENTIST = "GENERAL_DENTIST";

    @Autowired
    private TimeZoneContext timeZoneContext;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private AcademicDegreeDao academicDegreeDao;

    @Autowired
    private EducationDao educationDao;

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private WeekDayDao weekDayDao;

    @Autowired
    private BayAreaDao bayAreaDao;

    private Set<String> languages;

    private Set<String> academicDegrees;

    private Set<String> educations;

    private Set<String> subcategories;

    private Set<String> weekDays;

    private Set<String> bayAreas;

    @PostConstruct
    private void cacheLists() {
        languages = languageDao.findAll().stream().map(Language::getId).collect(Collectors.toSet());
        academicDegrees = academicDegreeDao.findAll().stream().map(AcademicDegree::getId).collect(Collectors.toSet());
        educations = educationDao.findAll().stream().map(Education::getId).collect(Collectors.toSet());
        subcategories = subCategoryDao.findAll().stream().map(SubCategory::getId).collect(Collectors.toSet());
        subcategories.remove(GENERAL_DENTIST);
        weekDays = weekDayDao.findAll().stream().map(WeekDay::getId).collect(Collectors.toSet());
        bayAreas = bayAreaDao.findAll().stream().map(BayArea::getId).collect(Collectors.toSet());
    }

    private MddRandomUtils() {
    }

    public Set<String> randomSubcategories() {
        return randomSubset(subcategories);
    }

    public String randomSubcategory() {
        return subcategories.iterator().next();
    }

    public Set<String> randomWeekDays() {
        return randomSubset(weekDays);
    }

    public String randomWeekDay() {
        return randomWeekDays().iterator().next();
    }

    public Set<String> randomBayAreas() {
        return randomSubset(bayAreas);
    }

    public String randomBayArea() {
        return randomBayAreas().iterator().next();
    }

    public Set<String> randomLanguages() {
        return randomSubset(languages);
    }

    public Set<String> randomEducations() {
        return randomSubset(educations);
    }

    public Set<String> randomAcademicDegrees() {
        return randomSubset(academicDegrees);
    }

    public String randomLanguage() {
        return randomLanguages().iterator().next();
    }

    public String randomEducation() {
        return randomEducations().iterator().next();
    }

    public String randomAcademicDegree() {
        return randomAcademicDegrees().iterator().next();
    }

    static <T> Set<T> randomSubset(Set<T> set) {
        List<T> list = new ArrayList<T>(set);
        Collections.shuffle(list);
        return new HashSet<>(list.subList(0, 2));
    }

    public List<WorkScheduleModel> getWorkSchedules() {
        return Stream.of(weekDays.toArray(new String[]{})).map(weekDay -> {

            ZonedDateTime zonedDateTime = ZonedDateTime.now().withZoneSameInstant(timeZoneContext.get());

            WorkScheduleModel workScheduleModel = new WorkScheduleModel();
            workScheduleModel.setWeekDay(weekDay);

            workScheduleModel.setStartTime(zonedDateTime.toLocalTime().plusSeconds(15));
            workScheduleModel.setEndTime(zonedDateTime.toLocalTime().plusMinutes(1));
            return workScheduleModel;
        }).collect(Collectors.toList());
    }
}