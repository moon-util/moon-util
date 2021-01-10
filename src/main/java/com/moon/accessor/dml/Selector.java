package com.moon.accessor.dml;

import com.moon.accessor.Condition;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Selector<R> {

    public Selector() {
    }

    public Selector<R> from(Table<R> table) {
        return this;
    }

    public WhereClauser where(Condition condition){
        return null;
    }
}
