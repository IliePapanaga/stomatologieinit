package com.cl.mdd.server.core.service.user.impl;

import com.cl.mdd.server.core.data.model.common.ProfessionalSubcategoryModel;
import com.cl.mdd.server.core.data.model.query.FindProfessionalSubcategories;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import com.cl.mdd.server.core.data.model.query.model.ProfessionalSubcategoryTuple;
import com.cl.mdd.server.core.data.persistent.access.prodessional.ProfessionalQuestionnaireDao;
import com.cl.mdd.server.core.data.persistent.access.specialty.SubCategoryDao;
import com.cl.mdd.server.core.data.persistent.access.user.CertificateDetailsDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.data.persistent.model.user.professional.questionnaire.ProfessionalQuestionnaire;
import com.cl.mdd.server.core.manager.user.ProfessionalManager;
import com.cl.mdd.server.core.security.annotation.RequiresProfessionalRole;
import com.cl.mdd.server.core.service.ServiceSupport;
import com.cl.mdd.server.core.service.user.ProfessionalSubcategoryService;
import com.cl.mdd.server.core.validation.group.Delete;
import com.cl.mdd.server.core.validation.group.Save;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;


@Service
@Validated
public class ProfessionalSubcategoryServiceImpl extends ServiceSupport implements ProfessionalSubcategoryService {

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private CertificateDetailsDao certificateDetailsDao;

    @Autowired
    private ProfessionalQuestionnaireDao professionalQuestionnaireDao;

    @Autowired
    private ProfessionalManager professionalManager;

    @Override
    @Transactional
    @RequiresProfessionalRole
    @Validated(value = Save.class)
    public void addProfessionalSubcategories(String professionalId, Set<String> subCategories) {
        Professional professional = professionalManager.findOne(professionalId);
        professional.getSubCategories().addAll(subCategories.stream().map(subCategoryDao::findOne).filter(Objects::nonNull).collect(toSet()));
        updateSpecialties(professional);
        userDao.updateLastActivityForCurrentUser();
    }

    @Override
    @Transactional
    @Validated(value = Delete.class)
    @PreAuthorize("@professionalAccessAuthorizer.updateAllowed(#professionalId)")
    public void deleteProfessionalSubcategory(String professionalId, String subcategoryId) {
        Professional professional = professionalManager.findOne(professionalId);
        professional.getSubCategories().remove(subCategoryDao.findOne(subcategoryId));
        updateSpecialties(professional);
        updateCertificates(professional);
        updateQuestionnaires(professional);
        userDao.updateLastActivityForCurrentUser();
    }

    private void updateCertificates(Professional professional) {
        certificateDetailsDao.removeAfterSubSubcategoryDeleted(professional);
    }

    private void updateQuestionnaires(Professional professional) {
        List<ProfessionalQuestionnaire> orphanQuestionnaires = professionalQuestionnaireDao.findAllProfessionalQuestionnaireAfterSubcategoryDeleted(professional);
        professionalQuestionnaireDao.delete(orphanQuestionnaires);
    }

    private void updateSpecialties(Professional professional) {
        String specialties = professional.getSubCategories().stream()
                .map(SubCategory::getName)
                .sorted()
                .collect(Collectors.joining(","));
        professional.setSpecialties(specialties);
    }

    @Override
    @PreAuthorize("@professionalSubCategoryAccessAuthorizer.readAllowed(#queryInfo.filters.professionalId)")
    public QueryResult<ProfessionalSubcategoryModel> fetch(FindProfessionalSubcategories queryInfo) {
        FindProfessionalSubcategories.FindProfessionalSubcategoriesFilter filters = queryInfo.getFilters();
        Pageable pageable = queryConverter.toPageable(queryInfo.getPagination());

        Page<ProfessionalSubcategoryTuple> subcategories = subCategoryDao.findSubcategories(filters.getProfessionalId(),
                pageable);
        return queryConverter.toQueryResult(subcategories, commonConverter::toProfessionalSubCategoryModel);
    }


}
