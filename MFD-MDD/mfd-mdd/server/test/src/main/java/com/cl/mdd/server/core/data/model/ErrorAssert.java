package com.cl.mdd.server.core.data.model;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;

import static com.cl.mdd.server.core.data.model.utils.JsonUtil.valueFromPath;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class ErrorAssert {
    public static final String NOT_NULL = "may not be null";


    private String json;
    private Collection<Error> errors;

    private ErrorAssert(String json) {
        this.json = json;
        init();
    }

    private void init() {
        errors = emptyIfNull(valueFromPath("errors", json, new TypeReference<List<Error>>() {
        }));
    }

    public static ErrorAssert of(String json) {
        Validate.notBlank(json);
        return new ErrorAssert(json);
    }

    public ErrorAssert andExpect(String message, String... path) {
        Validate.notBlank(message);
        Validate.notEmpty(path);

        errors.stream()
                .filter(error -> message.equals(error.getMessage()) &&emptyIfNull(error.getPath()).containsAll(asList(path)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No error with message '" + message + "' and path '" + asList(path) + "'"));

        return this;
    }


}
