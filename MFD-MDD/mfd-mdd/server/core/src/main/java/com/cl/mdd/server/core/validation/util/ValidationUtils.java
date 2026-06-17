package com.cl.mdd.server.core.validation.util;

import com.cl.mdd.server.core.exception.MDDException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ValidationUtils {

    /**
     * Validate filter params
     * <p/>
     * Checks if the params are all non null or all are null
     * @param params
     */
    public void validateFilterParam(String errorMessage, String errorCode, Object... params){
        List<Object> parameters = Arrays.asList(params);
        boolean valid = parameters.stream().allMatch(Objects::isNull) || parameters.stream().allMatch(Objects::nonNull);
        if(!valid){
            throw new MDDException(errorMessage, errorCode);
        }
    }
}
