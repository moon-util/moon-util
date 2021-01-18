package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public interface InsertIntoCol1<T1, R, TB extends Table<R, TB>> extends InsertInto<R, TB> {

    InsertIntoVal1<T1, R, TB> values(T1 v1);

    SelectCol1<T1> select(TableField<T1, ?, ? extends Table<?, ?>> f1);
}
