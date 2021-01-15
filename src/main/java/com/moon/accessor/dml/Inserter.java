package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class Inserter<R, TB extends Table<R, TB>> {

    private final TB table;

    public Inserter(TB table) { this.table = table; }

    public <V> InserterWither<R, TB> setWith(TableField<V, R, TB> field, R record) {
        return new InserterWither<>(table).setWith(field, record);
    }

    public <V> InserterWither<R, TB> set(TableField<V, R, TB> field, V value) {
        return new InserterWither<>(table).set(field, value);
    }

    public <V> InserterWither<R, TB> set(FieldRefer<TB, TableField<V, R, TB>> refer, V value) {
        return set(refer.ref(table), value);
    }

    public InserterCol<R, TB> columns(TableField<?, R, TB> field) {
        return new InserterCol<>(table).add(field);
    }

    public void values(R record) {
    }

    public void values(R record, R record1) {
    }

    public void values(R record, R record1, R... records) {
    }

    public void valuesOfAll(R... records) {
    }

    public void values(Iterable<R> values) {
    }
}
