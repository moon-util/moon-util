package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class TableUpdaterImpl<R, TB extends Table<R, TB>> implements TableUpdater<R, TB> {

    private final TB table;

    public TableUpdaterImpl(TB table) { this.table = table; }

    @Override
    public <T> TableUpdateSetter<R, TB> setNull(TableField<T, R, TB> field) {
        return null;
    }

    @Override
    public <T> TableUpdateSetter<R, TB> set(TableField<T, R, TB> field, T value) {
        return null;
    }

    @Override
    public <T> TableUpdateSetter<R, TB> set(TableField<T, R, TB> field, TableField<T, R, TB> sourceField) {
        return null;
    }
}
