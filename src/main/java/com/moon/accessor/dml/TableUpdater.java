package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public interface TableUpdater<R, TB extends Table<R, TB>> {

    <T> TableUpdateSetter<R, TB> setNull(TableField<T, R, TB> field);

    <T> TableUpdateSetter<R, TB> set(TableField<T, R, TB> field, T value);

    <T> TableUpdateSetter<R, TB> set(TableField<T, R, TB> field, TableField<T, R, TB> sourceField);
}
