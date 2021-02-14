package com.moon.processing.defaults;

import com.moon.accessor.annotation.TableField;
import com.moon.accessor.type.JdbcType;
import com.moon.accessor.type.TypeHandler;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 */
public class TableFieldEnum implements TableField {

    /** INSTANCE */
    public static final TableFieldEnum INSTANCE = new TableFieldEnum();

    @Override
    public String name() { return ""; }

    @Override
    public JdbcType jdbcType() { return JdbcType.AUTO; }

    @Override
    public Class<? extends TypeHandler> typeHandler() { return TypeHandler.class; }

    @Override
    public int length() { return -1; }

    @Override
    public int precision() { return -1; }

    @Override
    public Class<? extends Annotation> annotationType() { return TableField.class; }
}
