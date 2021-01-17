package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class SelectColsImpl extends TableFieldsHolder implements SelectCols {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public SelectColsImpl(TableField... fields) { super(fields); }

    @Override
    public <R1, TB1 extends Table<R1, TB1>> SelectColsFrom<R1, TB1> from(TB1 table) {
        return null;
    }

    @Override
    public <R1, TB1 extends Table<R1, TB1>> SelectColsFrom<R1, TB1> from(TB1 table, Table<?, ?>... tables) {
        return null;
    }
}
