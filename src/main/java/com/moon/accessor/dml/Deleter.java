package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Deleter<R> {

    private final Table<R> table;

    public Deleter(Table<R> table) {
        this.table = table;
    }
}
