package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol1Where<T1, R, TB extends Table<R, TB>> extends SelectCol1GroupBy<T1, R, TB> {

    SelectCol1GroupBy<T1, R, TB> groupBy();
}
