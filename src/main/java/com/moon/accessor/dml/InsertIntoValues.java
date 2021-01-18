package com.moon.accessor.dml;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.Collection;
import java.util.List;

/**
 * @author benshaoye
 */
public class InsertIntoValues<T1, T2, T3, T4, R, TB extends Table<R, TB>>
    extends InsertIntoColImpl<T1, T2, T3, T4, R, TB> {

    private final List<Object[]> values;

    InsertIntoValues(DSLConfiguration config, TB table, TableField<?, R, TB>[] fields, List<Object[]> values) {
        super(config, table, requireSameLength(fields, values));
        this.values = values;
    }

    private static <T> T[] requireSameLength(T[] fields, List<Object[]> valuesAll) {
        for (Object[] values : valuesAll) {
            requireSameLength(fields, values);
        }
        return fields;
    }

    private static <T> void requireSameLength(T[] fields, Object[] values) {
        if (values == null || fields.length != values.length) {
            throw new IllegalStateException("插入表字段值数量与字段长度不一致。");
        }
    }

    @Override
    public InsertIntoColImpl<T1, T2, T3, T4, R, TB> values(T1 v1) {
        valuesOf(this.values, v1);
        return this;
    }

    @Override
    public InsertIntoColImpl<T1, T2, T3, T4, R, TB> values(T1 v1, T2 v2) {
        valuesOf(this.values, v1, v2);
        return this;
    }

    @Override
    public InsertIntoCols<R, TB> valuesRecord(R record) {
        toValues(this.values, record);
        return this;
    }

    @SafeVarargs
    @Override
    public final InsertIntoCols<R, TB> valuesRecord(R record1, R record2, R... records) {
        toValues(this.values, zeroIfNull(records), record1, record2, records);
        return this;
    }

    @Override
    public InsertIntoCols<R, TB> valuesRecord(Collection<? extends R> records) {
        toValues(this.values, records);
        return this;
    }
}
