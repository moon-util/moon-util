package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.Collection;

/**
 * @author benshaoye
 */
public interface InsertIntoCol2<T1, T2, R, TB extends Table<R, TB>> extends InsertInto<R, TB> {

    InsertIntoVal2<T1, T2, R, TB> values(T1 v1, T2 v2);

    @Override
    InsertIntoVal2<T1, T2, R, TB> valuesRecord(R record);

    @Override
    InsertIntoVal2<T1, T2, R, TB> valuesRecord(R record1, R record2, R... records);

    @Override
    InsertIntoVal2<T1, T2, R, TB> valuesRecord(Collection<? extends R> record);

    SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    );
}
