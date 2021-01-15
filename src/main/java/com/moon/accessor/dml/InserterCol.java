package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.ArrayList;

/**
 * @author benshaoye
 */
public class InserterCol<R, TB extends Table<R, TB>> {

    private final TB table;
    private ArrayList<TableField<?, R, TB>> tableFields;

    InserterCol(TB table) {
        this.table = table;
    }

    private ArrayList<TableField<?, R, TB>> ensureTableFields() {
        return tableFields == null ? (tableFields = new ArrayList<>()) : tableFields;
    }

    InserterCol<R, TB> add(TableField<?, R, TB> field) {
        ensureTableFields().add(field);
        return this;
    }

    InserterCol<R, TB> addAll(TableField<?, R, TB>... fields) {
        if (fields != null) {
            ArrayList<TableField<?, R, TB>> tableFields = ensureTableFields();
            tableFields.ensureCapacity(tableFields.size() + fields.length);
            for (TableField<?, R, TB> field : fields) {
                tableFields.add(field);
            }
        }
        return this;
    }
}
