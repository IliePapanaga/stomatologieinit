package com.cl.mdd.server.core.service.notification;

import com.cl.mdd.server.core.data.persistent.model.posting.JobPosting;
import com.cl.mdd.server.core.data.persistent.model.specialty.SubCategory;
import com.cl.mdd.server.core.service.notification.definition.NotificationContextSupplier;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import com.cl.mdd.server.core.service.notification.definition.impl.BasePredefinedVariables;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JobPostingVariables extends BasePredefinedVariables implements NotificationContextSupplier<JobPosting> {

    public static final String JOB_POSTING_NAME_PLACEHOLDER = "{job.posting.name}";
    public static final String JOB_POSTING_SPECIALTIES = "{job.posting.specialties}";

    private static final List<Variable> JOB_POSTING_VARIABLES = Arrays.asList(
            variable("notification.var.job.posting.name", JOB_POSTING_NAME_PLACEHOLDER),
            variable("notification.var.job.posting.specialties", JOB_POSTING_SPECIALTIES)
    );

    @Override
    public List<Variable> expand() {
        return JOB_POSTING_VARIABLES;
    }

    @Override
    public void supply(JobPosting object, Map<String, String> context) {
        context.put(JOB_POSTING_NAME_PLACEHOLDER, object.getName());
        context.put(JOB_POSTING_SPECIALTIES, object.getSubCategories().stream().map(SubCategory::getName).sorted().collect(Collectors.joining(", ")));
    }
}
