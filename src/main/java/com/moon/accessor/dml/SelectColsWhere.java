package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectColsWhere<R, TB extends Table<R, TB>> extends SelectColsGroupBy<R, TB> {

    SelectColsGroupBy<R, TB> groupBy();
}
