package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
abstract class TableHolder<R, TB extends Table<R, TB>> {

    private final TB table;

    public TableHolder(TB table) {
        this.table = table;
    }

    public TB getTable() { return table; }
}
