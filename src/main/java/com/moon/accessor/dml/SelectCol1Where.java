package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol1Where<T1, R, TB extends Table<R, TB>> extends SelectCol1GroupBy<T1, R, TB> {

    SelectCol1Where<T1, R, TB> and(Conditional conditional);

    SelectCol1Where<T1, R, TB> or(Conditional conditional);

    SelectCol1GroupBy<T1, R, TB> groupBy();
}
