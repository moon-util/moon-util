package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public abstract class DSL {

    DSL() { }

    public static final <R, TB extends Table<R, TB>> InsertInto<R, TB> insertInto(TB table) {
        return new InsertIntoColsImpl<>(table, table.getTableFields());
    }

    public static final <T1, R, TB extends Table<R, TB>> InsertIntoCol1<T1, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1
    ) { return new InsertIntoColsImpl<>(table, f1); }

    public static final <T1, T2, R, TB extends Table<R, TB>> InsertIntoCol2<T1, T2, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1, TableField<T2, R, TB> f2
    ) { return new InsertIntoColsImpl<>(table, f1, f2); }
}
