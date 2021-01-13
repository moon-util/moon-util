package com.moon.accessor;

import com.moon.accessor.dml.Deleter;
import com.moon.accessor.dml.Inserter;
import com.moon.accessor.dml.Selector;
import com.moon.accessor.dml.Updater;
import com.moon.accessor.meta.TableField;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Session {

    public Session() {
    }

    public Selector select(TableField<?, ?, ? extends Table<?>>... fields) {
        return new Selector(fields);
    }

    public <T, R, TB extends Table<R>> Selector<R> select(TableField<T, R, TB> field) {
        return new Selector<>(field);
    }

    public Selector selectDistinct(TableField<?, ?, ? extends Table<?>>... fields) {
        return new Selector(true, fields);
    }

    public <T, R, TB extends Table<R>> Selector<R> selectDistinct(TableField<T, R, TB> field) {
        return new Selector<>(true, field);
    }

    public <R, T extends Table<R>> Inserter<R, T> insert(T table) {
        return new Inserter<>(table);
    }

    public <R, T extends Table<R>> Updater<R, T> update(T table) {
        return new Updater<>(table);
    }

    public <R, T extends Table<R>> Deleter<R, T> delete(T table) {
        return new Deleter<>(table);
    }
}
