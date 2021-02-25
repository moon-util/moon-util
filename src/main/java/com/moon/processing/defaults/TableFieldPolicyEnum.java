package com.moon.processing.defaults;

import com.moon.accessor.annotation.table.CasePolicy;
import com.moon.accessor.annotation.table.TableFieldPolicy;

/**
 * @author benshaoye
 */
public enum TableFieldPolicyEnum implements TableFieldPolicy {
    /** default instance */
    INSTANCE;

    @Override
    public String pattern() { return "{}"; }

    @Override
    public CasePolicy casePolicy() { return CasePolicy.UNDERSCORE_LOWERCASE; }

    @Override
    public Class<TableFieldPolicy> annotationType() { return TableFieldPolicy.class; }
}
