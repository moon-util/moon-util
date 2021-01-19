package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author benshaoye
 */
abstract class TableFieldsHolder<R, TB extends Table<R, TB>> {

    @SuppressWarnings("rawtypes")
    protected final static TableField[] FIELDS = {};
    private final TableField<?, R, TB>[] fields;

    @SuppressWarnings("unchecked")
    @SafeVarargs
    protected TableFieldsHolder(TableField<?, R, TB>... fields) {
        this.fields = fields == null ? FIELDS : fields;
    }

    @SafeVarargs
    protected static <R> List<R> asList(R... values) {
        return Arrays.asList(values);
    }

    @SafeVarargs
    protected static <R> List<R> asList(R record1, R record2, R... values) {
        List<R> list = new ArrayList<>();
        list.add(record1);
        list.add(record2);
        for (R value : values) {
            list.add(value);
        }
        return list;
    }

    protected static int length(Object[] values) { return values == null ? 0 : values.length; }

    protected static void requireLengthEquals(Object[] values1, Object[] values2, String message) {
        if (length(values1) != length(values2)) {
            throw new IllegalStateException(message);
        }
    }

    protected final List<Object[]> requireAddAll(List<Object[]> list, Object... values) {
        requireLengthEquals(getFields(), values, "数据长度与字段数不一致。");
        list.add(values);
        return list;
    }

    protected final List<Object[]> requireAddRecord(List<Object[]> list, Collection<? extends R> records) {
        for (R record : records) {
            requireAddRecord(list, record);
        }
        return list;
    }

    @SafeVarargs
    protected final List<Object[]> requireAddRecord(List<Object[]> list, R record1, R record2, R... records) {
        requireAddRecord(list, record1);
        requireAddRecord(list, record2);
        if (records != null) {
            for (R record : records) {
                requireAddRecord(list, record);
            }
        }
        return list;
    }

    protected final List<Object[]> requireAddRecord(List<Object[]> list, R record) {
        TableField<?, R, TB>[] fields = this.getFields();
        Object[] values = new Object[fields.length];
        for (int i = 0, l = fields.length; i < l; i++) {
            values[i] = fields[i].getPropertyValue(record);
        }
        list.add(values);
        return list;
    }

    public TableField<?, R, TB>[] getFields() { return fields; }
}
