package com.moon.accessor.dml;

import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Inserter<R, T extends Table<R>> {

    private final T table;

    public Inserter(T table) {
        this.table = table;
    }

    public <V> Inserter<R, T> set(Field<V, R, T> field, V value) {
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
