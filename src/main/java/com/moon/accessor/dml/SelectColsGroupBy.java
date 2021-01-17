package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectColsGroupBy<R, TB extends Table<R, TB>> extends SelectColsOrderBy<R, TB> {

    SelectColsOrderBy<R, TB> orderBy();

    SelectColsHaving<R, TB> having();
}
