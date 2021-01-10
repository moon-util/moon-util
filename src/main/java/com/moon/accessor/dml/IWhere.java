package com.moon.accessor.dml;

import com.moon.accessor.Condition;

/**
 * @author benshaoye
 */
public interface IWhere {

    /**
     * where 子句
     *
     * @return where 子句
     */
    WhereClause where();

    /**
     * where 子句
     *
     * @param condition where 条件
     *
     * @return where 子句
     */
    WhereClause where(Condition condition);
}
