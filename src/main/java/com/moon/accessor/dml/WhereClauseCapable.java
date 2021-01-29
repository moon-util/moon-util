package com.moon.accessor.dml;

import com.moon.accessor.Conditional;

/**
 * @author benshaoye
 */
public interface WhereClauseCapable<T> extends Done {

    /**
     * where 子句
     *
     * @param condition where 条件
     *
     * @return where 子句
     */
    T where(Conditional condition);
}
