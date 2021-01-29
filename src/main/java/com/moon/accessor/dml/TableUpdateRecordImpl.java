package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class TableUpdateRecordImpl<R, TB extends Table<R, TB>> extends TableUpdateFields<R, TB>
    implements TableUpdateRecord<R, TB> {

    public TableUpdateRecordImpl(TB table, R record) { super(table, transform(table, record)); }

    private static <R, TB extends Table<R, TB>> Map<TableField<?, R, TB>, Object> transform(TB table, R record) {
        Map<TableField<?, R, TB>, Object> setsMap = new LinkedHashMap<>();
        for (TableField<?, R, TB> tableField : table.getTableFields()) {
            setsMap.put(tableField, tableField.getPropertyValue(record));
        }
        return setsMap;
    }
}
