package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol1Having<T1, R, TB extends Table<R, TB>> extends SelectCol1OrderBy<T1, R, TB> {

    SelectCol1OrderBy<T1, R, TB> orderBy();
}
