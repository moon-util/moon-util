package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol2Having<T1, T2, R, TB extends Table<R, TB>> extends SelectCol2OrderBy<T1, T2, R, TB> {

    SelectCol2OrderBy<T1, T2, R, TB> orderBy(int count);
}
