package com.moon.accessor.dml;

import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Inserter<R> {

    private final Table<R> table;

    public Inserter(Table<R> table) {
        this.table = table;
    }

    public <T> Inserter<R> set(Field<T, R, ? extends Table<R>> field, T value) {
        return this;
    }

    public void values(R record) {
    }

    public void values(R record, R record1) {
    }

    public void values(R record, R record1, R... records) {
    }

    public void valuesOfAll(R... records) {
    }

    public void values(Iterable<R> values) {
    }
}
