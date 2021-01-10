package com.moon.accessor.dml;

import com.moon.accessor.Condition;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Deleter<R> implements IWhere {

    private final Table<R> table;

    public Deleter(Table<R> table) {
        this.table = table;
    }

    @Override
    public WhereClause where() {
        return null;
    }

    @Override
    public WhereClause where(Condition condition) {
        return null;
    }
}
