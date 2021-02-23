package com.moon.accessor.meta;

import com.moon.accessor.type.TypeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public class TableFields<R, TB extends Table<R, TB>> {

    private final TB table;
    private final int capacity;
    private final List<FieldSetter<?, R, TB>> fields;

    public TableFields(TB table, int capacity) {
        this.fields = new ArrayList<>(capacity);
        this.capacity = capacity;
        this.table = table;
    }

    public <T> void add(TableField<T, R, TB> field, T value) {
        fields.add(new FieldSetter<>(field.getTypeHandler(), value, 0));
    }

    public <T> void add(TableField<T, R, TB> field, T value, boolean available) {
        if (available) {
            add(field, value);
        }
    }

    private static class FieldSetter<T, R, TB extends Table<R, TB>> {

        private final TypeHandler<T> handler;
        private final T value;
        private final int index;

        private FieldSetter(TypeHandler<T> handler, T value, int index) {
            this.handler = handler;
            this.value = value;
            this.index = index;
        }
    }
}
