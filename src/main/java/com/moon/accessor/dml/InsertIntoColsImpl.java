package com.moon.accessor.dml;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.List;

/**
 * @author benshaoye
 */
public class InsertIntoColsImpl<T1, T2, R, TB extends Table<R, TB>>//
    extends InsertIntoBase<R, TB>//
    implements InsertInto<R, TB>, InsertIntoCol1<T1, R, TB>, InsertIntoCol2<T1, T2, R, TB> {

    public InsertIntoColsImpl(DSLConfiguration config, TB table, TableField<?, R, TB>... fields) {
        super(config, table, fields);
    }

    @Override
    public InsertIntoValuesImpl<T1, T2, R, TB> values(T1 v1) { return insertValuesOf(asList(v1)); }

    @Override
    public InsertIntoValuesImpl<T1, T2, R, TB> values(T1 v1, T2 v2) { return insertValuesOf(asList(v1, v2)); }

    private InsertIntoValuesImpl<T1, T2, R, TB> insertValuesOf(List<Object[]> values) {
        return new InsertIntoValuesImpl<>(getConfig(), getTable(), getFields(), values);
    }

    @Override
    public SelectCol1<T1> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1
    ) { return new SelectCol1Impl<>(f1); }

    @Override
    public SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    ) { return new SelectCol2Impl<>(f1, f2); }
}
