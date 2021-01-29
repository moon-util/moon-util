package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class TableDeleterImpl<R, TB extends Table<R, TB>> extends TableHolder<R, TB> implements TableDeleter<R, TB> {

    public TableDeleterImpl(TB table) { super(table); }

    @Override
    public TableDeleteWhere<R, TB> where(Conditional condition) { return new TableDeleteWhereImpl<>(getTable()); }

    @Override
    public int done() {
        return 0;
    }
}
