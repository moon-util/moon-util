package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class TableDeleteWhereImpl<R, TB extends Table<R, TB>> extends TableHolder<R, TB> implements TableDeleteWhere<R, TB> {

    public TableDeleteWhereImpl(TB table) { super(table); }

    @Override
    public TableDeleteWhere<R, TB> and(Conditional condition) {
        return this;
    }

    @Override
    public TableDeleteWhere<R, TB> or(Conditional condition) {
        return this;
    }

    @Override
    public int done() {
        return 0;
    }
}
