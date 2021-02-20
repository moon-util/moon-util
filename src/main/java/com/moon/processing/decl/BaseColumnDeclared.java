package com.moon.processing.decl;

import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
abstract class BaseColumnDeclared {

    public BaseColumnDeclared() {}

    protected final static Class<?> TABLE_FIELD_CLASS = TableField.class;

    @SuppressWarnings("all")
    protected final static String NEW_TABLE_FIELD =//
        "new {}<>(this, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})";
}
