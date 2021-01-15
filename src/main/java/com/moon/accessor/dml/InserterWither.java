package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public class InserterWither<R, TB extends Table<R, TB>> {

    private final TB table;
    private List<FieldEntry<?, R, TB>> tableFields;

    InserterWither(TB table) { this.table = table;}

    private List<FieldEntry<?, R, TB>> ensureTableFields() {
        return tableFields == null ? (tableFields = new ArrayList<>()) : tableFields;
    }

    public <V> InserterWither<R, TB> setWith(TableField<V, R, TB> field, R record) {
        return set(field, field.getValue(record));
    }

    public <V> InserterWither<R, TB> set(FieldRefer<TB, TableField<V, R, TB>> refer, V value) {
        return set(refer.ref(table), value);
    }

    public <V> InserterWither<R, TB> set(TableField<V, R, TB> field, V value) {
        ensureTableFields().add(new FieldEntry<>(field, value));
        return this;
    }
}
