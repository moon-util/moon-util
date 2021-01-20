package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol1Join<T1, R, TB extends Table<R, TB>> {

    SelectCol1From<T1, R, TB> on();
}
