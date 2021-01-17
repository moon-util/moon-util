package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class SelectCol4Impl<T1, T2, T3, T4> extends TableFieldsHolder implements SelectCol4<T1, T2, T3, T4> {

    public SelectCol4Impl(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2,
        TableField<T3, ?, ? extends Table<?, ?>> f3,
        TableField<T4, ?, ? extends Table<?, ?>> f4
    ) { super(f1, f2, f3, f4); }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol4From<T1, T2, T3, T4, R, TB> from(TB table) {
        return null;
    }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol4From<T1, T2, T3, T4, R, TB> from(
        TB table, Table<?, ?>... tables
    ) {
        return null;
    }
}
