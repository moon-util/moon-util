package com.moon.accessor.dml;

import com.moon.accessor.Condition;
import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.Table;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class Updater<R, T extends Table<R>> implements IWhere {

    private final T table;

    public Updater(T table) { this.table = table; }

    public <V> Updater<R, T> set(Field<V, R, T> field, V value) {
        return this;
    }

    public Updater<R, T> set(Consumer<T> consumer) {
        return this;
    }

    @Override
    public WhereClause where() {
        return null;
    }

    @Override
    public WhereClause where(Condition condition) {
        return null;
    }

    public int done() {
        return 0;
    }

    public Updater<R, T> then(Consumer<Integer> consumer) {
        return this;
    }

    public Updater<R, T> onCatch(Consumer<? extends Throwable> consumer) {
        return this;
    }
}
