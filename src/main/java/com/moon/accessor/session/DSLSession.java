package com.moon.accessor.session;

import com.moon.accessor.Supported;
import com.moon.accessor.config.Configuration;
import com.moon.accessor.dml.*;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.function.Function;

/**
 * @author benshaoye
 */
@Supported(value = 2, max = 16)
public class DSLSession {

    private static Function<Configuration, ? extends DSLSession> creator;

    static { creator = DSLSession::new; }

    private final Configuration config;

    public DSLSession(Configuration config) { this.config = config; }

    public static <T extends DSLSession> T newDSLSession(Configuration config) {
        return (T) creator.apply(config);
    }

    public final Configuration getConfig() { return config; }

    @SafeVarargs
    protected static <T> T[] toArr(T... ts) { return ts; }

    public final <R, TB extends Table<R, TB>> InsertInto<R, TB> insertInto(TB table) {
        return new InsertIntoColsImpl<>(getConfig(), table, table.getTableFields());
    }

    public final <T1, R, TB extends Table<R, TB>> InsertIntoCol1<T1, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1
    ) { return new InsertIntoColsImpl<>(getConfig(), table, f1); }

    public final <T1, T2, R, TB extends Table<R, TB>> InsertIntoCol2<T1, T2, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1, TableField<T2, R, TB> f2
    ) { return new InsertIntoColsImpl<>(getConfig(), table, f1, f2); }

    @SafeVarargs
    public final SelectCols select(TableField<?, ?, ? extends Table<?, ?>>... fields) {
        return new SelectColsImpl(fields);
    }

    public final <T1> SelectCol1<T1> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1
    ) { return null; }

    public <T1, T2> SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    ) { return null; }

    public <R, TB extends Table<R, TB>> TableUpdater<R, TB> update(TB table) { return new TableUpdaterImpl<>(table); }

    public <R, TB extends Table<R, TB>> TableDeleter<R, TB> delete(TB table) { return new TableDeleterImpl<>(table); }
}
