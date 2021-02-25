package com.moon.accessor.meta;

import com.moon.accessor.util.String2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public class JdbcParameters {

    private final int capacity;
    private final List<JdbcParameter<?>> fields;

    public JdbcParameters(int capacity) {
        this.fields = new ArrayList<>(capacity);
        this.capacity = capacity;
    }

    public static boolean isEmpty(JdbcParameters parameters) {
        return parameters == null || parameters.fields.isEmpty();
    }

    public <T> void add(TableField<T, ?, ?> field, T value) {
        fields.add(new JdbcValueParameter<>(field, value, fields.size()));
    }

    public <T> void resolveIfNull(TableField<T, ?, ?> field, T value, Supplier<T> fallbackProvider) {
        if (value == null) {
            add(field, fallbackProvider);
        } else {
            add(field, value);
        }
    }

    public <T extends CharSequence> void resolveIfEmpty(
        TableField<T, ?, ?> field, T value, Supplier<T> fallbackProvider
    ) {
        if (value == null || value.length() == 0) {
            add(field, fallbackProvider);
        } else {
            add(field, value);
        }
    }

    public <T extends CharSequence> void resolveIfBlank(
        TableField<T, ?, ?> field, T value, Supplier<T> fallbackProvider
    ) {
        if (String2.isBlank(value)) {
            add(field, fallbackProvider);
        } else {
            add(field, value);
        }
    }

    public <T> void addIfAvailable(TableField<T, ?, ?> field, T value, boolean available) {
        if (available) {
            add(field, value);
        }
    }

    public <T> void addIfMatched(
        TableField<T, ?, ?> field, T value, Predicate<T> matcher
    ) { addIfAvailable(field, value, matcher.test(value)); }

    public <T> void addIfNotNull(TableField<T, ?, ?> field, T value) {
        addIfAvailable(field, value, value != null);
    }

    public <S extends CharSequence> void addIfNotEmpty(
        TableField<S, ?, ?> field, S value
    ) { addIfAvailable(field, value, value != null && value.length() > 0); }

    public <S extends CharSequence> void addIfNotBlank(
        TableField<S, ?, ?> field, S value
    ) { addIfAvailable(field, value, String2.isNotBlank(value)); }

    public <T> void add(TableField<T, ?, ?> field, Supplier<T> valueProvider) {
        fields.add(new JdbcProviderParameter<>(field, valueProvider, fields.size()));
    }

    public int getCapacity() { return capacity; }

    public void setParameters(PreparedStatement stmt) throws SQLException {
        for (JdbcParameter<?> field : fields) {
            field.setParameter(stmt);
        }
    }

    public <T> T withForEach(T inputValue, BiConsumer<T, JdbcParameter<?>> parameterConsumer) {
        for (JdbcParameter<?> field : fields) {
            parameterConsumer.accept(inputValue, field);
        }
        return inputValue;
    }
}
