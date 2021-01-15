package com.moon.processor.model;

import com.moon.accessor.annotation.CasePolicy;
import com.moon.accessor.annotation.TablePolicy;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 */
public enum DefaultTablePolicy implements TablePolicy {
    /** default instance */
    INSTANCE;

    private final String[] EMPTY = {};

    @Override
    public String tables() { return "Tables"; }

    @Override
    public String pattern() { return "{}"; }

    @Override
    public String[] trimEntityPrefix() { return EMPTY; }

    @Override
    public String[] trimEntitySuffix() { return EMPTY; }

    @Override
    public CasePolicy casePolicy() { return CasePolicy.UNDERSCORE_LOWERCASE; }

    @Override
    public Class<TablePolicy> annotationType() { return TablePolicy.class; }
}
