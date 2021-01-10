package com.moon.accessor.dml;

import com.moon.accessor.Condition;

/**
 * @author benshaoye
 */
public class WhereClause {

    public WhereClause() {
    }

    public WhereClause and(Condition condition) {
        return this;
    }

    public WhereClause or(Condition condition) {
        return this;
    }
}
