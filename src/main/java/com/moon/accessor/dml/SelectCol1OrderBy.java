package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol1OrderBy<T1, R, TB extends Table<R, TB>> extends SelectCol1Limit<T1, R, TB> {

    SelectCol1Limit<T1, R, TB> limit(int count);

    SelectCol1Limit<T1, R, TB> limit(int offset, int count);
}
