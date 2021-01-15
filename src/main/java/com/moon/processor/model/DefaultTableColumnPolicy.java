package com.moon.processor.model;

import com.moon.accessor.annotation.CasePolicy;
import com.moon.accessor.annotation.TableColumnPolicy;

/**
 * @author benshaoye
 */
public enum DefaultTableColumnPolicy implements TableColumnPolicy {
    /** default instance */
    INSTANCE;

    @Override
    public String pattern() { return "{}"; }

    @Override
    public CasePolicy casePolicy() { return CasePolicy.UNDERSCORE_LOWERCASE; }

    @Override
    public Class<TableColumnPolicy> annotationType() { return TableColumnPolicy.class; }
}
