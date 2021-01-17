package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol1<T1> {

    <R, TB extends Table<R, TB>> SelectCol1From<T1, R, TB> from(TB table);

    <R, TB extends Table<R, TB>> SelectCol1From<T1, R, TB> from(TB table, Table<?, ?>... tables);
}
