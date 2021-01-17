package com.moon.processor.model;

import com.moon.accessor.annotation.CasePolicy;
import com.moon.accessor.annotation.TableFieldPolicy;

/**
 * @author benshaoye
 */
public enum DefaultTableColumnPolicy implements TableFieldPolicy {
    /** default instance */
    INSTANCE;

    @Override
    public String pattern() { return "{}"; }

    @Override
    public CasePolicy casePolicy() { return CasePolicy.UNDERSCORE_LOWERCASE; }

    @Override
    public Class<TableFieldPolicy> annotationType() { return TableFieldPolicy.class; }
}
