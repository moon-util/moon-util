package com.moon.accessor.dml;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @author benshaoye
 */
abstract class InsertIntoCols<R, TB extends Table<R, TB>> extends TableFieldsHolder<R, TB>
    implements InsertInto<R, TB> {

    private final DSLConfiguration config;
    private final TB table;

    @SafeVarargs
    InsertIntoCols(DSLConfiguration config, TB table, TableField<?, R, TB>... fields) {
        super(fields);
        this.config = config;
        this.table = table;
    }

    protected final DSLConfiguration getConfig() { return config; }

    public TB getTable() { return table; }

    protected final List<Object[]> toValues(List<Object[]> values, R record) {
        values.add(getValues(record));
        return values;
    }

    @SafeVarargs
    protected final List<Object[]> toValues(List<Object[]> values, int length, R r1, R r2, R... rs) {
        values.add(getValues(r1));
        values.add(getValues(r2));
        for (int i = 0; i < length; i++) {
            values.add(getValues(rs[i]));
        }
        return values;
    }

    protected final List<Object[]> toValues(List<Object[]> values, Collection<? extends R> records) {
        Objects.requireNonNull(records, "空集合");
        if (values instanceof ArrayList) {
            ((ArrayList<Object[]>) values).ensureCapacity(values.size() + records.size());
        }
        for (R record : records) {
            values.add(getValues(record));
        }
        return values;
    }

    @Override
    public InsertIntoCols<R, TB> valuesRecord(R record) {
        List<Object[]> valuesList = new ArrayList<>(2);
        valuesList.add(getValues(record));
        return new InsertIntoValues<>(getConfig(), getTable(), getFields(), valuesList);
    }

    @Override
    public InsertIntoCols<R, TB> valuesRecord(R record1, R record2, R... records) {
        int length = records == null ? 0 : records.length;
        List<Object[]> valuesList = new ArrayList<>(length + 2);
        toValues(valuesList, length, record1, record2, records);
        return new InsertIntoValues<>(getConfig(), getTable(), getFields(), valuesList);
    }

    @Override
    public InsertIntoCols<R, TB> valuesRecord(Collection<? extends R> records) {
        List<Object[]> values = records.stream().map(this::getValues).collect(toList());
        return new InsertIntoValues<>(getConfig(), getTable(), getFields(), values);
    }

}
