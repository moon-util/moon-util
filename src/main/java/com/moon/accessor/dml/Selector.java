package com.moon.accessor.dml;

import com.moon.accessor.Condition;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Selector<R> implements IWhere{

    public Selector() {
    }

    public Selector<R> from(Table<R> table) {
        return this;
    }

    @Override
    public WhereClause where() {
        return null;
    }

    @Override
    public WhereClause where(Condition condition){
        return null;
    }
}
