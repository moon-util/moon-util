package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
abstract class TableUpdateBase<R, TB extends Table<R, TB>> {

    private final TB table;

    public TableUpdateBase(TB table) {
        this.table = table;
    }

    public TB getTable() { return table; }
}
