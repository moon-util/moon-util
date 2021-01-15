package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public class Deleter<R, T extends Table<R, T>> implements WhereConditional<DeleterWhereClause> {

    private final T table;

    public Deleter(T table) {
        this.table = table;
    }

    @Override
    public DeleterWhereClause where() {
        return null;
    }

    @Override
    public DeleterWhereClause where(Conditional condition) {
        return null;
    }
}
