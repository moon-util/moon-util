package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol4OrderBy<T1, T2, T3, T4, R, TB extends Table<R, TB>> extends SelectCol4Limit<T1, T2, T3, T4, R, TB> {

    SelectCol4Limit<T1, T2, T3, T4, R, TB> limit(int count);

    SelectCol4Limit<T1, T2, T3, T4, R, TB> limit(int offset, int count);
}
