package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol2From<T1, T2, R, TB extends Table<R, TB>> extends SelectCol2Where<T1, T2, R, TB> {

    SelectCol2Where<T1, T2, R, TB> where();
}
