package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface InsertIntoVal1<T1, R, TB extends Table<R, TB>>
    extends InsertIntoValues<R, TB>, InsertIntoCol1<T1, R, TB>, Done {}

