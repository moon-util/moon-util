package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCols {

    <R, TB extends Table<R, TB>> SelectColsFrom<R, TB> from(TB table);

    <R, TB extends Table<R, TB>> SelectColsFrom<R, TB> from(TB table, Table<?, ?>... tables);
}
