package com.moon.accessor.dml;

import com.moon.accessor.Conditional;

/**
 * @author benshaoye
 */
public interface WhereClauseCapable {

    WhereClauseCapable and(Conditional condition);

    WhereClauseCapable or(Conditional condition);
}
