package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class TableUpdaterImpl<R, TB extends Table<R, TB>> extends TableUpdateBase<R, TB>
    implements TableUpdater<R, TB> {

    public TableUpdaterImpl(TB table) { super(table); }

    @Override
    public <T> TableUpdateSetter<R, TB> setIf(TableField<T, R, TB> field, T value, boolean doUpdate) {
        return new TableUpdateSetterImpl<>(getTable(), field, value, doUpdate);
    }

    @Override
    public <T> TableUpdateSetter<R, TB> setIf(
        TableField<T, R, TB> field, TableField<T, R, TB> sourceField, boolean doUpdate
    ) { return new TableUpdateSetterImpl<>(getTable(), field, sourceField, doUpdate); }
}
