package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol3<T1, T2, T3> {

    <R, TB extends Table<R, TB>> SelectCol3From<T1, T2, T3, R, TB> from(TB table);

    <R, TB extends Table<R, TB>> SelectCol3From<T1, T2, T3, R, TB> from(TB table, Table<?, ?>... tables);
}
