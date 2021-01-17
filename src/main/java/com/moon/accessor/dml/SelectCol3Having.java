package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol3Having<T1, T2, T3, R, TB extends Table<R, TB>> extends SelectCol3OrderBy<T1, T2, T3, R, TB> {

    SelectCol3OrderBy<T1, T2, T3, R, TB> orderBy();
}
