package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol2OrderBy<T1, T2, R, TB extends Table<R, TB>> {

    SelectCol2Limit<T1, T2, R, TB> limit(int count);

    SelectCol2Limit<T1, T2, R, TB> limit(int offset, int count);
}
