package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public interface TableUpdater<R, TB extends Table<R, TB>> {

    default <T> TableUpdateSetter<R, TB> setNull(TableField<T, R, TB> field) {
        return setNullIf(field, true);
    }

    default <T> TableUpdateSetter<R, TB> set(TableField<T, R, TB> field, T value) {
        return setIf(field, value, true);
    }

    default <T> TableUpdateSetter<R, TB> set(TableField<T, R, TB> field, TableField<T, R, TB> sourceField) {
        return setIf(field, sourceField, true);
    }

    default <T> TableUpdateSetter<R, TB> setRecordValue(TableField<T, R, TB> field, R record) {
        return setRecordValueIf(field, record, true);
    }

    /**
     * 当{@code doUpdate}为 true 时，才设置字段值为 null
     *
     * @param field    待更新字段
     * @param doUpdate 是否更新该字段，若值为 false，那么这个字段不会出现在最终 SQL 语句中，即不会更新这个字段
     * @param <T>      字段类型
     *
     * @return
     */
    default <T> TableUpdateSetter<R, TB> setNullIf(TableField<T, R, TB> field, boolean doUpdate) {
        return setIf(field, (T) null, doUpdate);
    }

    /**
     * 当{@code doUpdate}为 true 时，才设置字段为指定值
     *
     * @param field    待更新字段
     * @param value    要设置的字段值
     * @param doUpdate 是否更新该字段，若值为 false，那么这个字段不会出现在最终 SQL 语句中，即不会更新这个字段
     * @param <T>      字段类型
     *
     * @return
     */
    <T> TableUpdateSetter<R, TB> setIf(TableField<T, R, TB> field, T value, boolean doUpdate);

    /**
     * 当{@code doUpdate}为 true 时，设置字段值为另一个字段的值
     *
     * @param field       待更新字段
     * @param sourceField 引用的另一个字段
     * @param doUpdate    是否更新该字段，若值为 false，那么这个字段不会出现在最终 SQL 语句中，即不会更新这个字段
     * @param <T>         字段类型
     *
     * @return
     */
    <T> TableUpdateSetter<R, TB> setIf(TableField<T, R, TB> field, TableField<T, R, TB> sourceField, boolean doUpdate);

    /**
     * 当{@code doUpdate}为 true 时，设置字段值为一指定值，这个值从对应的实体中获得
     *
     * @param field    待更新字段
     * @param record   对应实体的实例
     * @param doUpdate 是否更新该字段，若值为 false，那么这个字段不会出现在最终 SQL 语句中，即不会更新这个字段
     * @param <T>      字段类型
     *
     * @return
     */
    default <T> TableUpdateSetter<R, TB> setRecordValueIf(TableField<T, R, TB> field, R record, boolean doUpdate) {
        return setIf(field, field.getPropertyValue(record), doUpdate);
    }
}
