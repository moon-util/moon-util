package com.moon.accessor.dml;

import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.Table;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class Updater<R> {

    private final Table<R> table;

    public Updater(Table<R> table) {
        this.table = table;
    }

    public <T> Updater<R> set(Field<T, R, ? extends Table<R>> field, T value) {
        return this;
    }

    public int done() {
        return 0;
    }

    public Updater then(Consumer<Integer> consumer){
        return this;
    }

    public Updater onCatch(Consumer<? extends Throwable> consumer) {
        return this;
    }
}
