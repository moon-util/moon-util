package com.moon.processing.defaults;

import com.moon.accessor.annotation.domain.CasePolicy;
import com.moon.accessor.annotation.domain.TableFieldPolicy;

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
