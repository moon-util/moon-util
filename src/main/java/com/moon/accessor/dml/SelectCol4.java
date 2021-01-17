package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol4<T1, T2, T3, T4> {

    <R, TB extends Table<R, TB>> SelectCol4From<T1, T2, T3, T4, R, TB> from(TB table);

    <R, TB extends Table<R, TB>> SelectCol4From<T1, T2, T3, T4, R, TB> from(TB table, Table<?, ?>... tables);
}
