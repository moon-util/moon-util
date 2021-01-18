package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public interface InsertIntoCol2<T1, T2, R, TB extends Table<R, TB>> extends InsertInto<R, TB> {

    InsertIntoVal2<T1, T2, R, TB> values(T1 v1, T2 v2);

    SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    );
}
