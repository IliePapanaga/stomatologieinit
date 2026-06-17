package com.cl.mdd.server.core.manager.converter;

import com.cl.mdd.server.core.data.model.query.Pagination;
import com.cl.mdd.server.core.data.model.query.QueryResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class QueryConverterTest {

    @Spy
    private QueryConverter testClass = new QueryConverter();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testToQueryResult() {
        List<String> elements = Arrays.asList("1", "2");
        Page<String> page = new PageImpl<String>(elements);
        Function<String , Integer> mapper = Integer::valueOf;
        Pagination pagination = new Pagination();
        doReturn(pagination).when(testClass).convertToPagination(page);

        QueryResult<Integer> result = testClass.toQueryResult(page, mapper);

        verify(testClass).convertToPagination(page);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getResult().contains(1));
        Assert.assertTrue(result.getResult().contains(2));
    }
}