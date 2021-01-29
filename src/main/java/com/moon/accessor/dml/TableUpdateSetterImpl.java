package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.LinkedHashMap;

/**
 * @author benshaoye
 */
public class TableUpdateSetterImpl<R, TB extends Table<R, TB>> extends TableUpdateFields<R, TB>
    implements TableUpdateSetter<R, TB> {

    public TableUpdateSetterImpl(TB table) { super(table, new LinkedHashMap<>()); }

    private TableUpdateSetter<R, TB> doUpdateWith(TableField<?, R, TB> field, Object value, boolean doUpdate) {
        if (doUpdate && field != null) {
            getSetsMap().put(field, value);
        }
        return this;
    }

    @Override
    public <T> TableUpdateSetter<R, TB> setIf(TableField<T, R, TB> field, T value, boolean doUpdate) {
        return doUpdateWith(field, value, doUpdate);
    }

    @Override
    public <T> TableUpdateSetter<R, TB> setIf(
        TableField<T, R, TB> field, TableField<T, R, TB> sourceField, boolean doUpdate
    ) { return doUpdateWith(field, sourceField, doUpdate); }
}
