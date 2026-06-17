package com.cl.sns.server.core.service.msg.content;

import org.apache.commons.lang3.Validate;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * Message details factory base implementation.
 * <p />
 * Knows how to initialize the message with context using SpEl
 */
public abstract class BaseMessageDetailsFactory implements MessageDetailsFactory {
    public static final String RECIPIENT_PARAM = "RECIPIENT";

    private MapAccessor mapAccessor = new MapAccessor();


     public String parse(String template, Map<String, String> context) {
        if(template == null || context == null) {
            return null;
        }
        for(Map.Entry<String, String> entry : context.entrySet()){
            template = template.replace(entry.getKey(),entry.getValue());
        }
        return template;
    }

}
