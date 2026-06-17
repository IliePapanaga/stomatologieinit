package com.cl.mdd.server.core.manager.questionnaire;

import com.cl.mdd.server.core.data.model.questionnaire.Questionnaire;
import com.cl.mdd.server.core.data.model.questionnaire.QuestionnaireModel;
import com.cl.mdd.server.core.data.persistent.access.specialty.CategoryDao;
import com.cl.mdd.server.core.data.persistent.model.specialty.Category;
import com.cl.mdd.server.core.exception.MDDException;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class QuestionnaireProcessorManager {

    private final CategoryDao categoryDao;

    private final List<QuestionnaireProcessor> processors;

    public QuestionnaireProcessorManager(CategoryDao categoryDao, List<QuestionnaireProcessor> processors) {
        this.categoryDao = categoryDao;
        this.processors = Collections.unmodifiableList(processors);
    }

    public <T extends QuestionnaireModel & Questionnaire> QuestionnaireProcessor<T> getConverter(T model) {
        return findProcessorByModel(model)
                .orElseThrow(notSupportedModelException(model));
    }

    public <T extends QuestionnaireModel & Questionnaire> QuestionnaireProcessor<T> getConverter(String categoryId) {
        return processors.stream()
                .filter(processor -> processor.relativeCategoryId().equals(categoryId))
                .findAny()
                .orElseThrow(notSupportedCategoryException(categoryId));
    }

    public <T extends QuestionnaireModel & Questionnaire> Category getCategoryByQuestionnaireModel(T model) {
        return findProcessorByModel(model)
                    .map(processor -> categoryDao.getOne(processor.relativeCategoryId()))
                .orElseThrow(notSupportedModelException(model));
    }

    private <T extends QuestionnaireModel & Questionnaire> Optional<QuestionnaireProcessor> findProcessorByModel(T model) {
        return processors.stream()
                .filter(processor -> processor.modelClass().equals(model.getClass()))
                .findAny();
    }

    private <T extends QuestionnaireModel & Questionnaire> Supplier<MDDException> notSupportedModelException(T model) {
        return () -> new MDDException("Questionnaire model of type \"" + model.getClass().getSimpleName() + "\" is not yet supported", "E_QUESTIONNAIRE_TYPE_NOT_SUPPORTED");
    }

    private Supplier<MDDException> notSupportedCategoryException(String categoryId) {
        return () -> new MDDException("Questionnaire for specialty \"" + categoryId + "\" is not yet supported", "E_QUESTIONNAIRE_TYPE_NOT_SUPPORTED");
    }
}
