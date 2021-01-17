package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class SelectCol1Impl<T1> extends TableFieldsHolder implements SelectCol1<T1> {

    public SelectCol1Impl(
        TableField<T1, ?, ? extends Table<?, ?>> f1
    ) { super(f1); }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol1From<T1, R, TB> from(TB table) {
        return null;
    }

    @Override
    public <R, TB extends Table<R, TB>> SelectCol1From<T1, R, TB> from(TB table, Table<?, ?>... tables) {
        return null;
    }
}
