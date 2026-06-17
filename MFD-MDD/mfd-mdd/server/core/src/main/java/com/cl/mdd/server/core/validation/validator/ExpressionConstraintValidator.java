package com.cl.mdd.server.core.validation.validator;

import com.cl.mdd.server.core.validation.constraint.ExpressionConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;

import static javax.validation.constraintvalidation.ValidationTarget.ANNOTATED_ELEMENT;
import static javax.validation.constraintvalidation.ValidationTarget.PARAMETERS;

/**
 * Validates the {@link ExpressionConstraint}.
 * <br />
 * The validated entity is root, i.e. its fields can be directly referenced. SpEL is used.
 */
@SupportedValidationTarget({ANNOTATED_ELEMENT, PARAMETERS})
public class ExpressionConstraintValidator implements ConstraintValidator<ExpressionConstraint, Object> {

    @Autowired
    private ApplicationContext applicationContext;

    protected Expression expression;

    @Override
    public void initialize(ExpressionConstraint constraintAnnotation) {
        expression = new SpelExpressionParser().parseExpression(constraintAnnotation.expression());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(value);
        evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));
        return expression.getValue(evaluationContext, Boolean.class);
    }
}
