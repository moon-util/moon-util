package com.moon.accessor;

import com.moon.accessor.dml.Deleter;
import com.moon.accessor.dml.Inserter;
import com.moon.accessor.dml.Selector;
import com.moon.accessor.dml.Updater;
import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Session {

    public Session() {
    }

    public Selector select(Field<?, ?, ? extends Table<?>>... fields) {
        return new Selector();
    }

    public <R> Inserter<R> insert(Table<R> table) {
        return new Inserter<>(table);
    }

    public <R, T extends Table<R>> Updater<R, T> update(T table) {
        return new Updater<>(table);
    }

    public <R> Deleter<R> delete(Table<R> table) {
        return new Deleter<>(table);
    }
}
