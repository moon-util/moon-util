package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectColsFrom<R, TB extends Table<R, TB>> extends SelectColsWhere<R, TB> {

    SelectColsWhere<R, TB> where();
}
