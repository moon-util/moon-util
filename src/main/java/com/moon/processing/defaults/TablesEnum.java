package com.moon.processing.defaults;

import com.moon.accessor.annotation.domain.Tables;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 */
public enum TablesEnum implements Tables {
    /** default instance */
    INSTANCE;

    @Override
    public String value() { return "Tables"; }

    @Override
    public Class<? extends Annotation> annotationType() { return Tables.class; }
}
