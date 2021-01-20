package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface InsertIntoVal2<T1, T2, R, TB extends Table<R, TB>>
    extends InsertIntoValues<R, TB>, InsertIntoCol2<T1, T2, R, TB>, Done {}
