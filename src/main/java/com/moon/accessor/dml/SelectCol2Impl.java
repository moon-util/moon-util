package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class SelectCol2Impl<T1, T2> extends TableFieldsHolder implements SelectCol2<T1, T2> {

    public SelectCol2Impl(
        TableField<T1, ?, ? extends Table<?, ?>> f1,
        TableField<T2, ?, ? extends Table<?, ?>> f2
    ) { super(f1, f2); }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol2From<T1, T2, R, TB> from(TB table) {
        return null;
    }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol2From<T1, T2, R, TB> from(
        TB table, Table<?, ?>... tables
    ) {
        return null;
    }
}
