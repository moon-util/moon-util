package com.moon.accessor.session;

import com.moon.accessor.Supported;
import com.moon.accessor.config.Configuration;
import com.moon.accessor.config.ConfigurationContext;
import com.moon.accessor.dml.*;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
@Supported(value = 2, max = 16)
public class DSLSessionImpl extends ConfigurationContext implements DSLSession {

    public DSLSessionImpl(Configuration configuration) { super(configuration); }

    @SuppressWarnings("unused")
    @SafeVarargs
    protected static <T> T[] toArr(T... ts) { return ts; }

    @Override
    public final <R, TB extends Table<R, TB>> InsertInto<R, TB> insertInto(TB table) {
        return new InsertIntoColsImpl<>(getConfig(), table, table.getTableFields());
    }

    @Override
    public final <T1, R, TB extends Table<R, TB>> InsertIntoCol1<T1, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1
    ) { return new InsertIntoColsImpl<>(getConfig(), table, f1); }

    @Override
    public final <T1, T2, R, TB extends Table<R, TB>> InsertIntoCol2<T1, T2, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1, TableField<T2, R, TB> f2
    ) { return new InsertIntoColsImpl<>(getConfig(), table, f1, f2); }

    @Override
    @SafeVarargs
    public final SelectCols select(TableField<?, ?, ? extends Table<?, ?>>... fields) {
        return new SelectColsImpl(fields);
    }

    @Override
    public final <T1> SelectCol1<T1> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1
    ) { return null; }

    @Override
    public <T1, T2> SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    ) { return null; }

    @Override
    public <R, TB extends Table<R, TB>> TableUpdater<R, TB> update(TB table) { return new TableUpdaterImpl<>(table); }

    @Override
    public <R, TB extends Table<R, TB>> TableDeleter<R, TB> delete(TB table) { return new TableDeleterImpl<>(table); }
}
