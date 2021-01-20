package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.Collection;

/**
 * @author benshaoye
 */
public interface InsertIntoCol1<T1, R, TB extends Table<R, TB>> extends InsertInto<R, TB> {

    InsertIntoVal1<T1, R, TB> values(T1 v1);

    @Override
    InsertIntoVal1<T1, R, TB> valuesRecord(R record);

    @Override
    InsertIntoVal1<T1, R, TB> valuesRecord(R record1, R record2, R... records);

    @Override
    InsertIntoVal1<T1, R, TB> valuesRecord(Collection<? extends R> record);

    SelectCol1<T1> select(TableField<T1, ?, ? extends Table<?, ?>> f1);
}
