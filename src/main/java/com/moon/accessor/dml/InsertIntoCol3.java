package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public interface InsertIntoCol3<T1, T2, T3, R, TB extends Table<R, TB>> extends InsertInto<R, TB> {

    InsertIntoCol3<T1, T2, T3, R, TB> values(T1 v1, T2 v2, T3 v3);

    SelectCol3<T1, T2, T3> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2,
        TableField<T3, ?, ? extends Table<?, ?>> f3
    );
}
