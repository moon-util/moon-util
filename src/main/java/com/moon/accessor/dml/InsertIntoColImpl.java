package com.moon.accessor.dml;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.List;

/**
 * @author benshaoye
 */
public class InsertIntoColImpl<T1, T2, T3, T4, R, TB extends Table<R, TB>>//
    extends InsertIntoCols<R, TB>//
    implements InsertInto<R, TB>,
               InsertIntoCol1<T1, R, TB>,
               InsertIntoCol2<T1, T2, R, TB>,
               InsertIntoCol3<T1, T2, T3, R, TB>,
               InsertIntoCol4<T1, T2, T3, T4, R, TB> {

    @SafeVarargs
    public InsertIntoColImpl(DSLConfiguration config, TB table, TableField<?, R, TB>... fields) {
        super(config, table, fields);
    }

    @Override
    public InsertIntoColImpl<T1, T2, T3, T4, R, TB> values(T1 v1) {
        return insertValuesOf(valuesOf(v1));
    }

    @Override
    public InsertIntoColImpl<T1, T2, T3, T4, R, TB> values(T1 v1, T2 v2) {
        return insertValuesOf(valuesOf(v1, v2));
    }

    @Override
    public InsertIntoColImpl<T1, T2, T3, T4, R, TB> values(T1 v1, T2 v2, T3 v3) {
        return insertValuesOf(valuesOf(v1, v2, v3));
    }

    @Override
    public InsertIntoColImpl<T1, T2, T3, T4, R, TB> values(T1 v1, T2 v2, T3 v3, T4 v4) {
        return insertValuesOf(valuesOf(v1, v2, v3, v4));
    }

    private InsertIntoColImpl<T1, T2, T3, T4, R, TB> insertValuesOf(List<Object[]> values) {
        return new InsertIntoValues<>(getConfig(), getTable(), getFields(), values);
    }

    @Override
    public SelectCol1<T1> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1
    ) { return new SelectCol1Impl<>(f1); }

    @Override
    public SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    ) { return new SelectCol2Impl<>(f1, f2); }

    @Override
    public SelectCol3<T1, T2, T3> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2,
        TableField<T3, ?, ? extends Table<?, ?>> f3
    ) { return new SelectCol3Impl<>(f1, f2, f3); }

    @Override
    public SelectCol4<T1, T2, T3, T4> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2,
        TableField<T3, ?, ? extends Table<?, ?>> f3,
        TableField<T4, ?, ? extends Table<?, ?>> f4
    ) { return new SelectCol4Impl<>(f1, f2, f3, f4); }

    @Override
    public int done() {
        throw new UnsupportedOperationException("没有任何数据插入");
    }
}
