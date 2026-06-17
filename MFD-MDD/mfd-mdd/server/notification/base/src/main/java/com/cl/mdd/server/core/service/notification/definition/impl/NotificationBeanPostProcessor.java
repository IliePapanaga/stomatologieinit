package com.cl.mdd.server.core.service.notification.definition.impl;

import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import com.cl.mdd.server.core.data.model.notification.NotificationTypeVariableModel;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinitions;
import com.cl.mdd.server.core.service.notification.definition.PredefinedVariables;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.*;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Post processor for {@link NotificationDefinition} that populates {@link NotificationTypeDescriptorRegistry} with notifications supported by application
 */
public class NotificationBeanPostProcessor implements Ordered, MessageSourceAware, BeanPostProcessor, ApplicationContextAware {

    protected final Log logger = LogFactory.getLog(getClass());

    private ApplicationContext context;

    private MessageSource messageSource;

    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));


    private final NotificationTypeDescriptorRegistry registry;

    @Autowired
    public NotificationBeanPostProcessor(final NotificationTypeDescriptorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        if (!nonAnnotatedClasses.contains(targetClass)) {
            Map<Method, Set<NotificationDefinition>> annotatedMethods = selectAnnotatedMethods(targetClass);

            if (annotatedMethods.isEmpty()) {
                nonAnnotatedClasses.add(targetClass);
            } else {
                annotatedMethods.forEach((key, value) -> value.stream()
                        .forEach(notification -> processNotification(beanName, key, notification)));
            }
        }

        return bean;
    }

    protected Map<Method, Set<NotificationDefinition>> selectAnnotatedMethods(Class<?> targetClass) {
        return MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<NotificationDefinition>>) method -> {
                    Set<NotificationDefinition> notificationMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, NotificationDefinition.class, NotificationDefinitions.class);
                    return (!notificationMethods.isEmpty() ? notificationMethods : null);
                });
    }

    protected void processNotification(String beanName, Method method, NotificationDefinition notification) {
        logger.debug("Processing notification annotation on bean " + beanName + " method " + method.getName());

        registry.put(convertNotification(notification));
    }

    public NotificationTypeDescriptorModel convertNotification(NotificationDefinition notification) {

        NotificationTypeDescriptorModel model = new NotificationTypeDescriptorModel();

        model.setType(notification.value());
        model.setName(resolveName(notification.value(), notification.name()));
        model.setDescription(resolveDescription(notification.value(), notification.description()));

        List<NotificationTypeVariableModel> variables = new ArrayList<>();
        variables.addAll(convertVariables(notification.vars()));
        variables.addAll(convertPredefined(notification.predefined()));

        model.setVariables(variables);

        return model;
    }

    protected List<NotificationTypeVariableModel> convertVariables(Variable[] variables) {
        if (variables == null) {
            return Collections.emptyList();
        }

        return convertVariablesStream(Arrays.stream(variables));
    }

    protected List<NotificationTypeVariableModel> convertVariablesStream(Stream<Variable> variables) {
        return variables
                .map(this::convertVariable)
                .collect(Collectors.toList());
    }

    private NotificationTypeVariableModel convertVariable(Variable variable) {
        return new NotificationTypeVariableModel(variable.macro(), resolveMessage(variable.name()));
    }

    protected List<NotificationTypeVariableModel> convertPredefined(Class<? extends PredefinedVariables>[] predefined) {
        if (predefined == null) {
            return Collections.emptyList();
        }

        Stream<Variable> variableStream = Arrays.stream(predefined)
                .map(this::getPredefinedVariablesFromContext)
                .flatMap(bean -> bean.expand().stream());
        return convertVariablesStream(variableStream);
    }


    private PredefinedVariables getPredefinedVariablesFromContext(Class<? extends PredefinedVariables> predefinedVariableClass) {
        try {
            return context.getBean(predefinedVariableClass);
        } catch (final BeansException ex) {
            throw new IllegalArgumentException("No unique bean of type " + predefinedVariableClass.getSimpleName() + " found in application context", ex);
        }
    }

    private String resolveMessage(String message) {
        return this.messageSource.getMessage(message, new Object[0], message, Locale.getDefault());
    }

    private String resolveName(String type, String name) {
        if (StringUtils.isEmpty(name)) {
            return resolveMessageOrEmpty(type + ".name");
        } else {
            return resolveMessage(name);
        }
    }

    private String resolveDescription(String type, String description) {
        if (StringUtils.isEmpty(description)) {
            return resolveMessageOrEmpty(type + ".description");
        } else {
            return resolveMessage(description);
        }
    }

    private String resolveMessageOrEmpty(String message) {
        return this.messageSource.getMessage(message, new Object[0], "", Locale.getDefault());
    }
}
