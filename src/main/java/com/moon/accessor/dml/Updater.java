package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.TableField;
import com.moon.accessor.meta.Table;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class Updater<R, T extends Table<R, T>> implements WhereConditional<UpdaterWhereClause> {

    private final T table;

    public Updater(T table) { this.table = table; }

    public <V> Updater<R, T> set(TableField<V, R, T> field, V value) {
        return this;
    }

    public Updater<R, T> set(Consumer<T> consumer) {
        return this;
    }

    @Override
    public UpdaterWhereClause where() {
        return null;
    }

    @Override
    public UpdaterWhereClause where(Conditional condition) {
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
