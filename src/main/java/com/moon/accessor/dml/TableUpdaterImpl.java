package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class TableUpdaterImpl<R, TB extends Table<R, TB>> extends TableHolder<R, TB>
    implements TableUpdater<R, TB> {

    public TableUpdaterImpl(TB table) { super(table); }

    protected TableUpdateSetter<R, TB> newUpdateSetterImpl() {
        return new TableUpdateSetterImpl<>(getTable());
    }

    @Override
    public TableUpdateRecord<R, TB> setRecord(R record) {
        return new TableUpdateRecordImpl<>(getTable(), record);
    }

    // @Override
    // public TableUpdateSetter<R, TB> setRecordFields(
    //     R record, TableField<?, R, TB> field, TableField<?, R, TB>... fields
    // ) { return newUpdateSetterImpl().setRecordFields(record, field, fields); }

    /**
     * {@inheritDoc}
     *
     * @param field    待更新字段
     * @param value    要设置的字段值
     * @param doUpdate 是否更新该字段，若值为 false，那么这个字段不会出现在最终 SQL 语句中，即不会更新这个字段
     * @param <T>
     *
     * @return
     */
    @Override
    public <T> TableUpdateSetter<R, TB> setIf(TableField<T, R, TB> field, T value, boolean doUpdate) {
        return newUpdateSetterImpl().setIf(field, value, doUpdate);
    }

    /**
     * {@inheritDoc}
     *
     * @param field       待更新字段
     * @param sourceField 引用的另一个字段
     * @param doUpdate    是否更新该字段，若值为 false，那么这个字段不会出现在最终 SQL 语句中，即不会更新这个字段
     * @param <T>
     *
     * @return
     */
    @Override
    public <T> TableUpdateSetter<R, TB> setIf(
        TableField<T, R, TB> field, TableField<T, R, TB> sourceField, boolean doUpdate
    ) { return newUpdateSetterImpl().setIf(field, sourceField, doUpdate); }
}
