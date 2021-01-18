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
    implements InsertInto<R, TB>, InsertIntoVal1<T1, R, TB>, InsertIntoVal2<T1, T2, R, TB> {

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
    public int done() {
        return 0;
    }
}
