package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class FieldEntry<V, R, TB extends Table<R, TB>> {

    private final TableField<V, R, TB> field;
    private final V value;

    public FieldEntry(TableField<V, R, TB> field, V value) {
        this.field = field;
        this.value = value;
    }

    public TableField<V, R, TB> getField() { return field; }

    public V getValue() { return value; }
}
