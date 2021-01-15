package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class SelectorFromClause implements WhereConditional<SelectorWhereClause> {

    public SelectorFromClause() {
    }

    public SelectorFromClause leftJoin(Table<?, ?> table) {
        return this;
    }

    @Override
    public SelectorWhereClause where() {
        return null;
    }

    @Override
    public SelectorWhereClause where(Conditional condition) {
        return null;
    }

}
