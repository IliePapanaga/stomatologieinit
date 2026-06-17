package com.cl.sns.server.core.model.api;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BaseModel {
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                ToStringStyle.DEFAULT_STYLE, true, true);
    }
}
