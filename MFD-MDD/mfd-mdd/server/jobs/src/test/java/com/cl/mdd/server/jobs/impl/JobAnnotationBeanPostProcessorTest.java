package com.cl.mdd.server.jobs.impl;

import com.cl.mdd.server.jobs.Job;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.core.Ordered;
import org.springframework.util.StringValueResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobAnnotationBeanPostProcessorTest {

    private JobAnnotationBeanPostProcessor processor;

    @Mock
    private StringValueResolver stringValueResolver;

    @Mock
    private QuartzTriggerRegistry triggerRegistry;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        processor = spy(new JobAnnotationBeanPostProcessor(triggerRegistry));
        processor.setEmbeddedValueResolver(stringValueResolver);

        doAnswer((Answer<String>) invocation -> (String) invocation.getArguments()[0]).when(stringValueResolver).resolveStringValue(anyString());
    }

    @Test
    public void getOrder_lowestPrecedence() {
        assertEquals(Ordered.LOWEST_PRECEDENCE, processor.getOrder());
    }


    @Test
    public void postProcessBeforeInitialization_doNoting() {
        Object bean = new Object();
        assertSame(bean, processor.postProcessBeforeInitialization(bean, "beanName"));
        verifyZeroInteractions(triggerRegistry, stringValueResolver);
    }

    @Test
    public void postProcessAfterInitialization_whenBeanIsNotAnnotated_skipProcessing() {
        Object bean = new Object();

        doReturn(Collections.emptyMap()).when(processor).selectAnnotatedMethods(any(Class.class));

        processor.postProcessAfterInitialization(bean, "beanName");

        verifyZeroInteractions(triggerRegistry, stringValueResolver);
    }

    @Test
    public void postProcessAfterInitialization_whenBeanHasAnnotatedMethod_performProcessing() throws NoSuchMethodException {
        Object bean = new Object();
        Method method1 = getMethod();
        Method method2 = getMethod2();
        Job job1 = buildJobAnnotation();
        Job job2 = buildJobAnnotation();

        doReturn(new HashMap<Method, Set<Job>>() {
            {
                put(method1, Collections.singleton(job1));
                put(method2, Collections.singleton(job2));
            }
        }).when(processor).selectAnnotatedMethods(any(Class.class));

        doNothing().when(processor).processJob(any(Job.class), any(Method.class), anyString());

        processor.postProcessAfterInitialization(bean, "beanName");

        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        ArgumentCaptor<Method> methodCaptor = ArgumentCaptor.forClass(Method.class);
        ArgumentCaptor<String> beanNameCaptor = ArgumentCaptor.forClass(String.class);

        verify(processor, times(2)).processJob(jobCaptor.capture(), methodCaptor.capture(), beanNameCaptor.capture());

        List<Job> jobs = jobCaptor.getAllValues();
        List<Method> methods = methodCaptor.getAllValues();
        List<String> beanNames = beanNameCaptor.getAllValues();

        assertSame(job1, jobs.get(0));
        assertSame(method1, methods.get(0));
        assertEquals("beanName", beanNames.get(0));

        assertSame(job2, jobs.get(1));
        assertSame(method2, methods.get(1));
        assertEquals("beanName", beanNames.get(1));
    }

    @Test
    public void processJob_whenInitialDelayIsNotLong_throwException() throws NoSuchMethodException {
        Method method = getMethod();

        expectedException.expect(IllegalArgumentException.class);

        processor.processJob(buildJobAnnotation("", "", "", "NAN", "NAME"), method, "beanName");
    }

    @Test
    public void processJob_whenFixedRateIsNotLong_throwException() throws NoSuchMethodException {
        Method method = getMethod();

        expectedException.expect(IllegalArgumentException.class);

        processor.processJob(buildJobAnnotation("", "", "NAN", "", "NAME"), method, "beanName");
    }

    @Test
    public void processJob_whenMethodHasParams_throwException() throws NoSuchMethodException {
        Method method = getMethodWithParams();

        expectedException.expect(IllegalArgumentException.class);

        processor.processJob(buildJobAnnotation(), method, "beanName");
    }

    @Test
    public void processJob_whenNeitherCronNorFixedRateIsDefined_throwException() throws NoSuchMethodException {
        Method method = getMethod();

        expectedException.expect(IllegalArgumentException.class);

        processor.processJob(buildJobAnnotation(), method, "beanName");
    }

    @Test
    public void processJob_whenBothCronAndFixedRateAreDefined_throwException() throws NoSuchMethodException {
        Method method = getMethod();

        expectedException.expect(IllegalArgumentException.class);

        processor.processJob(buildJobAnnotation("CRON", "TZ", "110000", "111", ""), method, "beanName");
    }

    @Test
    public void processJob_whenCronIsDefinedWoTZ_addCronTrigger() throws NoSuchMethodException {
        Method method = getMethod();

        processor.processJob(buildJobAnnotation("CRON", "", "", "", "job1"), method, "beanName");

        verify(triggerRegistry).addCronTrigger("beanName", "method", "job1", "CRON", TimeZone.getDefault());
    }

    @Test
    public void processJob_whenCronIsDefinedWithTZ_addCronTrigger() throws NoSuchMethodException {
        Method method = getMethod();

        processor.processJob(buildJobAnnotation("CRON", "UTC", "", "", "job1"), method, "beanName");

        verify(triggerRegistry).addCronTrigger("beanName", "method", "job1", "CRON", TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void processJob_whenFixedRateIsDefined_addFixedDelayTrigger() throws NoSuchMethodException {
        Method method = getMethod();

        processor.processJob(buildJobAnnotation("", "", "5000", "1000", ""), method, "beanName");

        verify(triggerRegistry).addFixedRateTrigger("beanName", "method", "beanName_method", 5000, 1000);
    }

    private Method getMethod() throws NoSuchMethodException {
        return TestClass.class.getMethod("method");
    }

    private Method getMethod2() throws NoSuchMethodException {
        return TestClass.class.getMethod("method2");
    }

    private Method getMethodWithParams() throws NoSuchMethodException {
        return TestClass.class.getMethod("methodWithParams", String.class);
    }

    private Job buildJobAnnotation() {
        return buildJobAnnotation(null, null, null, null, null);
    }

    private Job buildJobAnnotation(String cron, String zone, String fixedRate, String initialDelay, String name) {
        return new Job() {
            @Override
            public String cron() {
                return cron;
            }

            @Override
            public String zone() {
                return zone;
            }

            @Override
            public String fixedRate() {
                return fixedRate;
            }

            @Override
            public String initialDelay() {
                return initialDelay;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Job.class;
            }
        };
    }

    private static class TestClass {
        public void method() {

        }

        public void method2() {

        }

        public void methodWithParams(String param) {

        }
    }
}