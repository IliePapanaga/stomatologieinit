package com.cl.mdd.server.contract.pdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

@RunWith(MockitoJUnitRunner.class)
public class TemplateTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private Template template;

    @Test
    public void testClientFields() {
        Assert.assertEquals(
                new HashSet<>(Arrays.asList("a", "b", "signature")),
                template.clientFields(
                        Arrays.asList("a", "b", "signature_1", "signature_2", "current_date_1", "current_date_2")));
    }

}
