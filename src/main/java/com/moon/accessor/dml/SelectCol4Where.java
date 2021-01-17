package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol4Where<T1, T2, T3, T4, R, TB extends Table<R, TB>> extends SelectCol4GroupBy<T1, T2, T3, T4, R, TB> {

    SelectCol4GroupBy<T1, T2, T3, T4, R, TB> groupBy();
}
