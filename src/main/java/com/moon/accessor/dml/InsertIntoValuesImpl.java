package com.moon.accessor.dml;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.Collection;
import java.util.List;

/**
 * @author benshaoye
 */
public class InsertIntoValuesImpl<T1, T2, R, TB extends Table<R, TB>> extends InsertIntoBase<R, TB>
    implements InsertInto<R, TB>, InsertIntoVal1<T1, R, TB>, InsertIntoVal2<T1, T2, R, TB> {

    private final List<Object[]> values;

    InsertIntoValuesImpl(DSLConfiguration config, TB table, TableField<?, R, TB>[] fields, List<Object[]> values) {
        super(config, table, requireSameLength(fields, values));
        this.values = values;
    }

    private static <T> T[] requireSameLength(T[] fields, List<Object[]> valuesAll) {
        for (Object[] values : valuesAll) {
            requireLengthEquals(fields, values, "插入表字段值数量与字段长度不一致。");
        }
        return fields;
    }

    @Override
    public InsertIntoValuesImpl<T1, T2, R, TB> values(T1 v1) {
        requireAddAll(this.values, v1);
        return this;
    }

    @Override
    public InsertIntoValuesImpl<T1, T2, R, TB> values(T1 v1, T2 v2) {
        requireAddAll(this.values, v1, v2);
        return this;
    }

    @Override
    public SelectCol1<T1> select(TableField<T1, ?, ? extends Table<?, ?>> f1) {
        return null;
    }

    @Override
    public SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    ) {
        return null;
    }

    @Override
    public InsertIntoValuesImpl<T1, T2, R, TB> valuesRecord(R record) {
        toValues(this.values, record);
        return this;
    }

    @SafeVarargs
    @Override
    public final InsertIntoValuesImpl<T1, T2, R, TB> valuesRecord(R record1, R record2, R... records) {
        toValues(this.values, length(records), record1, record2, records);
        return this;
    }

    @Override
    public InsertIntoValuesImpl<T1, T2, R, TB> valuesRecord(Collection<? extends R> records) {
        toValues(this.values, records);
        return this;
    }

    @Override
    public int done() {
        return 0;
    }
}
