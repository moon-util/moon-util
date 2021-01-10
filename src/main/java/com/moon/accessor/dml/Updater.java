package com.moon.accessor.dml;

import com.moon.accessor.Condition;
import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.Table;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class Updater<R> implements IWhere {

    private final Table<R> table;

    public Updater(Table<R> table) {
        this.table = table;
    }

    public <T> Updater<R> set(Field<T, R, ? extends Table<R>> field, T value) {
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

    public Updater<R> then(Consumer<Integer> consumer) {
        return this;
    }

    public Updater<R> onCatch(Consumer<? extends Throwable> consumer) {
        return this;
    }
}
