package com.moon.processing.defaults;

import com.moon.accessor.annotation.CasePolicy;
import com.moon.accessor.annotation.TableModelPolicy;

/**
 * @author benshaoye
 */
public enum TableModelPolicyEnum implements TableModelPolicy {
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
    public Class<TableModelPolicy> annotationType() { return TableModelPolicy.class; }
}
