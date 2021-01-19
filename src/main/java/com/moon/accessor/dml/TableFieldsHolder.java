package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.ArrayList;
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

    protected final List<Object[]> asList(Object... values) {
        return requireAddAll(new ArrayList<>(2), values);
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

    protected final Object[] getRecordValues(R record) {
        TableField<?, R, TB>[] fields = this.getFields();
        Object[] values = new Object[fields.length];
        for (int i = 0, l = fields.length; i < l; i++) {
            values[i] = fields[i].getPropertyValue(record);
        }
        return values;
    }

    public TableField<?, R, TB>[] getFields() { return fields; }
}
