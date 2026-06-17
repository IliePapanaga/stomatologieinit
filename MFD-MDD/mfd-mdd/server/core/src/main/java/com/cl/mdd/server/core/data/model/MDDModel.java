package com.cl.mdd.server.core.data.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * MDD Base model
 * <p/>
 */
public abstract class MDDModel {

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                ToStringStyle.DEFAULT_STYLE, true, true);
    }
}
