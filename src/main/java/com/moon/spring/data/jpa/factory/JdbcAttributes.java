package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.annotation.JdbcDelete;
import com.moon.spring.data.jpa.annotation.JdbcInsert;
import com.moon.spring.data.jpa.annotation.JdbcSelect;
import com.moon.spring.data.jpa.annotation.JdbcUpdate;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 */
public class JdbcAttributes implements JdbcSelect, JdbcInsert, JdbcUpdate, JdbcDelete {

    private final Class<? extends Annotation> type;
    private final String value;

    private JdbcAttributes(String value, Class<? extends Annotation> type) {
        this.value = value;
        this.type = type;
    }

    private JdbcAttributes(JdbcSelect attrs) {
        this(attrs.value(), JdbcSelect.class);
    }

    private JdbcAttributes(JdbcInsert attrs) {
        this(attrs.value(), JdbcInsert.class);
    }

    private JdbcAttributes(JdbcUpdate attrs) {
        this(attrs.value(), JdbcUpdate.class);
    }

    private JdbcAttributes(JdbcDelete attrs) {
        this(attrs.value(), JdbcDelete.class);
    }

    public static JdbcAttributes of(JdbcSelect attrs) {
        return attrs instanceof JdbcAttributes ? (JdbcAttributes) attrs : new JdbcAttributes(attrs);
    }

    public static JdbcAttributes of(JdbcInsert attrs) {
        return attrs instanceof JdbcAttributes ? (JdbcAttributes) attrs : new JdbcAttributes(attrs);
    }

    public static JdbcAttributes of(JdbcUpdate attrs) {
        return attrs instanceof JdbcAttributes ? (JdbcAttributes) attrs : new JdbcAttributes(attrs);
    }

    public static JdbcAttributes of(JdbcDelete attrs) {
        return attrs instanceof JdbcAttributes ? (JdbcAttributes) attrs : new JdbcAttributes(attrs);
    }

    public JdbcStrategy getJdbcStrategy() {
        if (type == JdbcSelect.class) {
            return JdbcStrategy.SELECT;
        }
        if (type == JdbcInsert.class) {
            return JdbcStrategy.INSERT;
        }
        if (type == JdbcUpdate.class) {
            return JdbcStrategy.UPDATE;
        }
        if (type == JdbcDelete.class) {
            return JdbcStrategy.DELETE;
        }
        throw new IllegalStateException("Unknown annotated type of: " + type);
    }

    @Override
    public Class<? extends Annotation> annotationType() { return type; }

    @Override
    public String value() { return value; }
}
