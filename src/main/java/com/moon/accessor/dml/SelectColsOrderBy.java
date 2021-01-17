package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectColsOrderBy<R, TB extends Table<R, TB>> extends SelectColsLimit<R, TB> {

    SelectColsLimit<R, TB> limit(int count);

    SelectColsLimit<R, TB> limit(int offset, int count);
}
