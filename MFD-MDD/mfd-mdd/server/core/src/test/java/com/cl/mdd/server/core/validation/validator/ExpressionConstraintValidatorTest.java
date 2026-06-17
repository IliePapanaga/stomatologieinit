package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.data.persistent.model.user.professional.Professional;
import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionConstraintValidatorTest {

    @Spy
    @InjectMocks
    protected ExpressionConstraintValidator validator;

    @Mock
    protected ExpressionConstraint annotation;

    @Mock
    protected ApplicationContext applicationContext;

    @Test
    public void testInitialize() {
        Mockito.when(annotation.expression()).thenReturn("true");
        validator.initialize(annotation);
        Assert.assertNotNull(validator.expression);
    }

    @Test(expected = ParseException.class)
    public void testInitializeInvalid() {
        Mockito.when(annotation.expression()).thenReturn("some invalid expression");
        validator.initialize(annotation);
    }

    @Test
    public void testIsValid() {
        Professional professional = new Professional();
        validator.expression = new SpelExpressionParser().parseExpression("status == 'ACTIVE' and username == 'test'");
        Assert.assertFalse(validator.isValid(professional, null));
        professional.setStatus("ACTIVE");
        Assert.assertFalse(validator.isValid(professional, null));
        professional.setUsername("test");
        Assert.assertTrue(validator.isValid(professional, null));
    }
}