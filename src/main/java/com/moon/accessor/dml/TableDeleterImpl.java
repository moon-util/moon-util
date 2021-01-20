package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class TableDeleterImpl<R, TB extends Table<R, TB>> implements TableDeleter<R, TB> {

    private final TB table;

    public TableDeleterImpl(TB table) {
        this.table = table;
    }

    @Override
    public int done() {
        return 0;
    }
}
