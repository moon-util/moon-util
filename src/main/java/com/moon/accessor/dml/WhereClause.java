package com.moon.accessor.dml;

import com.moon.accessor.Conditional;

/**
 * @author benshaoye
 */
public interface WhereClause<C extends WhereClause<C>> {

    C and(Conditional condition);

    C or(Conditional condition);
}
