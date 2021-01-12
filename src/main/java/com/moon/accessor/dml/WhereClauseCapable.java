package com.moon.accessor.dml;

import com.moon.accessor.Conditional;

/**
 * @author benshaoye
 */
public class WhereClauseCapable {

    public WhereClauseCapable() {
    }

    public WhereClauseCapable and(Conditional condition) {
        return this;
    }

    public WhereClauseCapable or(Conditional condition) {
        return this;
    }
}
