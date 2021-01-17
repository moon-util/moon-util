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

    protected static List<Object[]> valuesOf(Object... values) {
        return valuesOf(new ArrayList<>(2), values);
    }

    protected static int zeroIfNull(Object[] values) {
        return values == null ? 0 : values.length;
    }

    protected static List<Object[]> valuesOf(List<Object[]> list, Object... values) {
        list.add(values);
        return list;
    }

    protected final Object[] getValues(R record) {
        TableField<?, R, TB>[] fields = this.fields;
        Object[] values = new Object[fields.length];
        for (int i = 0, l = fields.length; i < l; i++) {
            values[i] = fields[i].getPropertyValue(record);
        }
        return values;
    }

    public TableField<?, R, TB>[] getFields() { return fields; }
}
