package com.cl.mdd.server.core.service.notification.definition.impl;

import com.cl.mdd.server.core.data.model.notification.NotificationTypeDescriptorModel;
import com.cl.mdd.server.core.service.notification.definition.NotificationDefinition;
import com.cl.mdd.server.core.service.notification.definition.PredefinedVariables;
import com.cl.mdd.server.core.service.notification.definition.Variable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@RunWith(MockitoJUnitRunner.class)
public class NotificationBeanPostProcessorTest {

    private NotificationBeanPostProcessor processor;

    @Mock
    private ApplicationContext context;

    @Mock
    private MessageSource messageSource;

    @Mock
    private NotificationTypeDescriptorRegistry registry;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        processor = spy(new NotificationBeanPostProcessor(registry));

        processor.setMessageSource(messageSource);
        processor.setApplicationContext(context);
    }

    @Test
    public void getOrder() {
        assertEquals(LOWEST_PRECEDENCE, processor.getOrder());
    }

    @Test
    public void postProcessBeforeInitialization() {
        Object bean = new Object();

        assertSame(bean, processor.postProcessBeforeInitialization(bean, "name"));
    }

    @Test
    public void postProcessAfterInitialization_whenBeanIsNotAnnotated_skipProcessing() {
        Object bean = new Object();

        doReturn(Collections.emptyMap()).when(processor).selectAnnotatedMethods(any(Class.class));

        processor.postProcessAfterInitialization(bean, "beanName");

        verifyZeroInteractions(context, messageSource, registry);
    }

    @Test
    public void postProcessAfterInitialization_whenBeanHasAnnotatedMethod_performProcessing() throws NoSuchMethodException {
        Object bean = new Object();
        Method method1 = getMethod();
        Method method2 = getMethod2();
        NotificationDefinition def1 = buildNotificationDefinitionAnnotation();
        NotificationDefinition def2 = buildNotificationDefinitionAnnotation();

        doReturn(new HashMap<Method, Set<NotificationDefinition>>() {
            {
                put(method1, Collections.singleton(def1));
                put(method2, Collections.singleton(def2));
            }
        }).when(processor).selectAnnotatedMethods(any(Class.class));

        doNothing().when(processor).processNotification(anyString(), any(Method.class), any(NotificationDefinition.class));

        processor.postProcessAfterInitialization(bean, "beanName");

        ArgumentCaptor<NotificationDefinition> definitionCaptor = ArgumentCaptor.forClass(NotificationDefinition.class);
        ArgumentCaptor<Method> methodCaptor = ArgumentCaptor.forClass(Method.class);
        ArgumentCaptor<String> beanNameCaptor = ArgumentCaptor.forClass(String.class);

        verify(processor, times(2)).processNotification(beanNameCaptor.capture(), methodCaptor.capture(), definitionCaptor.capture());

        List<NotificationDefinition> definitions = definitionCaptor.getAllValues();
        List<Method> methods = methodCaptor.getAllValues();
        List<String> beanNames = beanNameCaptor.getAllValues();

        assertTrue(definitions.contains(def1));
        assertTrue(definitions.contains(def2));
        assertTrue(methods.contains(method1));
        assertTrue(methods.contains(method2));
        assertTrue(beanNames.contains("beanName"));
    }

    @Test
    public void processNotification_whenPredefinedClassNotInContext_throwException() throws NoSuchMethodException {
        Method method = getMethod();

        NotificationDefinition annotation = buildNotificationDefinitionAnnotation(
                "type", "name", "description", null,
                new Class[] { TestPredefinedVariables.class });

        doThrow(new NoSuchBeanDefinitionException("beanName")).when(context).getBean(eq(TestPredefinedVariables.class));

        expectedException.expect(IllegalArgumentException.class);

        processor.processNotification("beanName", method, annotation);
    }

    @Test
    public void processNotification_whenNoVariablesAndPredefinedNotPresent_addNotificationToRegistry() throws NoSuchMethodException {
        Method method = getMethod();

        doAnswer(invocation -> invocation.getArgumentAt(0, String.class)).when(messageSource).getMessage(anyString(), any(Object[].class), anyString(), any(Locale.class));

        NotificationDefinition annotation = buildNotificationDefinitionAnnotation(
                "type", "name", "description", null, null);

        processor.processNotification("beanName", method, annotation);

        ArgumentCaptor<NotificationTypeDescriptorModel> descriptorCaptor = ArgumentCaptor.forClass(NotificationTypeDescriptorModel.class);

        verify(registry).put(descriptorCaptor.capture());

        NotificationTypeDescriptorModel descriptor = descriptorCaptor.getValue();

        assertEquals("name", descriptor.getName());
        assertEquals("description", descriptor.getDescription());
        assertEquals("type", descriptor.getType());
        assertTrue(descriptor.getVariables().isEmpty());
    }

    @Test
    public void processNotification_whenVariablesFound_addNotificationToRegistry() throws NoSuchMethodException {
        Method method = getMethod();

        doAnswer(invocation -> invocation.getArgumentAt(0, String.class)).when(messageSource).getMessage(anyString(), any(Object[].class), anyString(), any(Locale.class));

        NotificationDefinition annotation = buildNotificationDefinitionAnnotation(
                "type", "name", "description",
                new Variable[] {
                        buildVariable("name1", "macro1"),
                        buildVariable("name2", "macro2"),
                }, null);

        processor.processNotification("beanName", method, annotation);

        ArgumentCaptor<NotificationTypeDescriptorModel> descriptorCaptor = ArgumentCaptor.forClass(NotificationTypeDescriptorModel.class);

        verify(registry).put(descriptorCaptor.capture());

        NotificationTypeDescriptorModel descriptor = descriptorCaptor.getValue();

        assertEquals("name", descriptor.getName());
        assertEquals("description", descriptor.getDescription());
        assertEquals("type", descriptor.getType());
        assertEquals(2, descriptor.getVariables().size());
        assertEquals("name1", descriptor.getVariables().get(0).getName());
        assertEquals("macro1", descriptor.getVariables().get(0).getVariable());
        assertEquals("name2", descriptor.getVariables().get(1).getName());
        assertEquals("macro2", descriptor.getVariables().get(1).getVariable());
    }

    @Test
    public void processNotification_whenPredefinedFound_addNotificationToRegistry() throws NoSuchMethodException {
        Method method = getMethod();

        doAnswer(invocation -> invocation.getArgumentAt(0, String.class)).when(messageSource).getMessage(anyString(), any(Object[].class), anyString(), any(Locale.class));

        NotificationDefinition annotation = buildNotificationDefinitionAnnotation(
                "type", "name", "description", null, new Class[] { TestPredefinedVariables.class});

        doReturn(new TestPredefinedVariables()).when(context).getBean(eq(TestPredefinedVariables.class));

        processor.processNotification("beanName", method, annotation);

        ArgumentCaptor<NotificationTypeDescriptorModel> descriptorCaptor = ArgumentCaptor.forClass(NotificationTypeDescriptorModel.class);

        verify(registry).put(descriptorCaptor.capture());

        NotificationTypeDescriptorModel descriptor = descriptorCaptor.getValue();

        assertEquals("name", descriptor.getName());
        assertEquals("description", descriptor.getDescription());
        assertEquals("type", descriptor.getType());
        assertEquals(2, descriptor.getVariables().size());
        assertEquals("name3", descriptor.getVariables().get(0).getName());
        assertEquals("macro3", descriptor.getVariables().get(0).getVariable());
        assertEquals("name4", descriptor.getVariables().get(1).getName());
        assertEquals("macro4", descriptor.getVariables().get(1).getVariable());
    }

    private Method getMethod() throws NoSuchMethodException {
        return TestClass.class.getMethod("method");
    }

    private Method getMethod2() throws NoSuchMethodException {
        return TestClass.class.getMethod("method2");
    }

    private NotificationDefinition buildNotificationDefinitionAnnotation() {
        return buildNotificationDefinitionAnnotation(null, null, null, null, null);
    }

    private NotificationDefinition buildNotificationDefinitionAnnotation(String value, String name, String description, Variable[] vars,
                                                                         Class<? extends PredefinedVariables>[] predefined) {
        return new NotificationDefinition() {
            @Override
            public String value() {
                return value;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public Variable[] vars() {
                return vars;
            }

            @Override
            public Class<? extends PredefinedVariables>[] predefined() {
                return predefined;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return NotificationDefinition.class;
            }
        };
    }

    private static Variable buildVariable(String name, String macro) {
        return new Variable() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String macro() {
                return macro;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Variable.class;
            }
        };
    }


    private static class TestClass {
        public void method() {
        }

        public void method2() {
        }
    }

    private static class TestPredefinedVariables implements PredefinedVariables {
        @Override
        public List<Variable> expand() {
            return Arrays.asList(
                    NotificationBeanPostProcessorTest.buildVariable("name3", "macro3"),
                    NotificationBeanPostProcessorTest.buildVariable("name4", "macro4")
            );
        }
    }
}