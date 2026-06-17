package com.cl.mdd.server.mvc.rest.graphql.advices;

import com.cl.mdd.server.core.data.model.errors.ErrorInfoModel;
import com.cl.mdd.server.core.exception.MDDException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

import static org.junit.Assert.*;

public class RestErrorHandlerAdviceTest {

    @Spy
    @InjectMocks
    private RestErrorHandlerAdvice testClass = new RestErrorHandlerAdvice();

    @Mock
    private AccessDeniedException accessDeniedException;

    @Mock
    private FieldError objectError;

    @Mock
    private BindException bindException;

    @Mock
    private MDDException mddException;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleAccessDenied() {
        String localizedMessage = "localMessage";
        Mockito.when(accessDeniedException.getLocalizedMessage()).thenReturn(localizedMessage);

        ResponseEntity<Collection<ErrorInfoModel>> result = testClass.handleAccessDenied(accessDeniedException);

        ErrorInfoModel error = result.getBody().iterator().next();
        Assert.assertSame(error.getMessage(), localizedMessage);
        Assert.assertEquals(error.getErrorCode(), HttpStatus.FORBIDDEN.toString());
    }

    @Test
    public void testHandleMDDException() {
        String code = "code";
        String localizedMessage = "localMessage";
        Mockito.when(mddException.getLocalizedMessage()).thenReturn(localizedMessage);
        Mockito.when(mddException.getCode()).thenReturn(code);

        ResponseEntity<Collection<ErrorInfoModel>> result = testClass.handleMDDException(mddException);

        ErrorInfoModel error = result.getBody().iterator().next();
        Assert.assertSame(error.getMessage(), localizedMessage);
        Assert.assertSame(error.getErrorCode(), code);
    }

    @Test
    public void testHandleBindException() {
        String code = "code";
        String localizedMessage = "localMessage";
        List<ObjectError> allErrors = Collections.singletonList(objectError);
        Mockito.when(bindException.getAllErrors()).thenReturn(allErrors);
        Mockito.when(objectError.getDefaultMessage()).thenReturn(localizedMessage);
        Mockito.when(objectError.getCode()).thenReturn(code);

        ResponseEntity<Object> result = testClass.handleBindException(bindException, null, HttpStatus.BAD_REQUEST, null);

        ErrorInfoModel error = ((Collection<ErrorInfoModel>)result.getBody()).iterator().next();
        Assert.assertSame(error.getMessage(), localizedMessage);
        Assert.assertSame(error.getErrorCode(), code);
    }
}