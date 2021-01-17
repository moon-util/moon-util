package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class SelectCol3Impl<T1, T2, T3> extends TableFieldsHolder implements SelectCol3<T1, T2, T3> {

    public SelectCol3Impl(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2,
        TableField<T3, ?, ? extends Table<?, ?>> f3
    ) { super(f1, f2, f3); }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol3From<T1, T2, T3, R, TB> from(TB table) {
        return null;
    }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol3From<T1, T2, T3, R, TB> from(
        TB table, Table<?, ?>... tables
    ) {
        return null;
    }
}
