package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public class TableUpdateSetterImpl<R, TB extends Table<R, TB>> extends TableUpdateBase<R, TB>
    implements TableUpdateSetter<R, TB> {

    private final List<TableField<?, R, TB>> fields = new ArrayList<>();
    private final List<Object> values = new ArrayList<>();

    public TableUpdateSetterImpl(TB table, TableField<?, R, TB> field, Object value, boolean doUpdate) {
        super(table);
        doUpdateWith(field, value, doUpdate);
    }

    private void doUpdateWith(TableField<?, R, TB> field, Object value, boolean doUpdate) {
        if (doUpdate) {
            fields.add(field);
            values.add(value);
        }
    }

    @Override
    public TableUpdateWhere<R, TB> where(Conditional conditional) {
        List<Object> valuesList = this.values;
        List<TableField<?, R, TB>> fieldsList = this.fields;
        TableField<?, R, TB>[] fields = fieldsList.toArray(new TableField[fieldsList.size()]);
        Object[] values = fieldsList.toArray(new Object[valuesList.size()]);
        fieldsList.clear();
        valuesList.clear();
        return new TableUpdateWhereImpl<>(getTable(), fields, values, conditional);
    }

    @Override
    public <T> TableUpdateSetter<R, TB> setIf(TableField<T, R, TB> field, T value, boolean doUpdate) {
        doUpdateWith(field, value, doUpdate);
        return this;
    }

    @Override
    public <T> TableUpdateSetter<R, TB> setIf(
        TableField<T, R, TB> field, TableField<T, R, TB> sourceField, boolean doUpdate
    ) {
        doUpdateWith(field, sourceField, doUpdate);
        return this;
    }

    @Override
    public int done() {
        return 0;
    }
}
