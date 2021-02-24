package com.moon.accessor.meta;

import com.moon.accessor.util.String2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
public class JdbcParameters<R, TB extends Table<R, TB>> {

    private final TB table;
    private final int capacity;
    private final List<JdbcParameter<?, R, TB>> fields;

    public JdbcParameters(TB table, int capacity) {
        this.fields = new ArrayList<>(capacity);
        this.capacity = capacity;
        this.table = table;
    }

    public <T> void add(TableField<T, R, TB> field, T value) {
        fields.add(new JdbcParameter<>(field, value, fields.size()));
    }

    public <T> void addIfAvailable(TableField<T, R, TB> field, T value, boolean available) {
        if (available) {
            add(field, value);
        }
    }

    public <T> void addIfValueMatched(TableField<T, R, TB> field, T value, Predicate<T> matcher) {
        addIfAvailable(field, value, matcher.test(value));
    }

    public <T> void addIfNotNull(TableField<T, R, TB> field, T value) {
        addIfAvailable(field, value, value != null);
    }

    public <S extends CharSequence> void addIfNotEmpty(TableField<S, R, TB> field, S value) {
        addIfAvailable(field, value, value != null && value.length() > 0);
    }

    public <S extends CharSequence> void addIfNotBlank(TableField<S, R, TB> field, S value) {
        addIfAvailable(field, value, String2.isNotBlank(value));
    }

    public TB getTable() { return table; }

    public int getCapacity() { return capacity; }

    public void setParameters(PreparedStatement stmt) throws SQLException {
        for (JdbcParameter<?, R, TB> field : fields) {
            field.setParameter(stmt);
        }
    }
}
