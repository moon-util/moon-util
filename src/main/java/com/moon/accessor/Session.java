package com.moon.accessor;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.dml.*;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
@Supported(4)
public class Session {

    private final DSLConfiguration config;

    public Session(DSLConfiguration config) { this.config = config; }

    public final DSLConfiguration getConfig() { return config; }

    @SafeVarargs
    protected static <T> T[] toArr(T... ts) { return ts; }

    public final <R, TB extends Table<R, TB>> InsertInto<R, TB> insertInto(TB table) {
        return new InsertIntoColImpl<>(getConfig(), table, table.getTableFields());
    }

    public final <T1, R, TB extends Table<R, TB>> InsertIntoCol1<T1, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1
    ) { return new InsertIntoColImpl<>(getConfig(), table, f1); }

    public final <T1, T2, R, TB extends Table<R, TB>> InsertIntoCol2<T1, T2, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1, TableField<T2, R, TB> f2
    ) { return new InsertIntoColImpl<>(getConfig(), table, f1, f2); }

    public final <T1, T2, T3, R, TB extends Table<R, TB>> InsertIntoCol3<T1, T2, T3, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1, TableField<T2, R, TB> f2, TableField<T3, R, TB> f3
    ) { return new InsertIntoColImpl<>(getConfig(), table, toArr(f1, f2, f3)); }

    public final <T1, T2, T3, T4, R, TB extends Table<R, TB>> InsertIntoCol4<T1, T2, T3, T4, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1, TableField<T2, R, TB> f2, TableField<T3, R, TB> f3, TableField<T4, R, TB> f4
    ) { return new InsertIntoColImpl<>(getConfig(), table, f1, f2, f3, f4); }

    @SafeVarargs
    public final SelectCols select(TableField<?, ?, ? extends Table<?, ?>>... fields) {
        return new SelectColsImpl(fields);
    }

    public final <T1> SelectCol1<T1> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1
    ) { return new SelectCol1Impl<>(f1); }

    public <T1, T2> SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    ) { return new SelectCol2Impl<>(f1, f2); }

    public final <T1, T2, T3> SelectCol3<T1, T2, T3> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2,
        TableField<T3, ?, ? extends Table<?, ?>> f3
    ) { return new SelectCol3Impl<>(f1, f2, f3); }

    public final <T1, T2, T3, T4> SelectCol4<T1, T2, T3, T4> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2,
        TableField<T3, ?, ? extends Table<?, ?>> f3,
        TableField<T4, ?, ? extends Table<?, ?>> f4
    ) { return new SelectCol4Impl<>(f1, f2, f3, f4); }

    public <R, T extends Table<R, T>> Updater<R, T> update(T table) {
        return new Updater<>(table);
    }

    public <R, T extends Table<R, T>> Deleter<R, T> delete(T table) {
        return new Deleter<>(table);
    }
}
