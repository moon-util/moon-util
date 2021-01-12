package com.moon.accessor.dml;

import com.moon.accessor.Conditional;

/**
 * @author benshaoye
 */
public interface WhereConditional<T extends WhereClauseCapable> {

    /**
     * where 子句
     *
     * @return where 子句
     */
    T where();

    /**
     * where 子句
     *
     * @param condition where 条件
     *
     * @return where 子句
     */
    T where(Conditional condition);
}
