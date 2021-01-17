package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol2<T1, T2> {

    <R, TB extends Table<R, TB>> SelectCol2From<T1, T2, R, TB> from(TB table);

    <R, TB extends Table<R, TB>> SelectCol2From<T1, T2, R, TB> from(TB table, Table<?, ?>... tables);
}
