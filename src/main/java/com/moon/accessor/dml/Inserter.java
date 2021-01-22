package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public class Inserter<R,TB extends Table<R,TB>> {

    private final static String INSERT = "INSERT INTO {} ({})";

    private final TB table;
    private final TableField<?, R, TB>[] fields;
    private final List<Object[]> values;

    public Inserter(TB table, TableField<?, R, TB>[] fields, List<Object[]> values) {
        this.table = table;
        this.fields = fields;
        this.values = new ArrayList<>(values);
    }

    private String getSQLStatement(List<Object> params) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        columns.append("INSERT INTO ");
        columns.append(table.getTableName());
        columns.append(" (");
        values.append(" (");
        TableField<?, R, TB>[] fields = this.fields;
        for (int i = 0, len = fields.length; i < len; i++) {
            TableField<?, R, TB> field = fields[i];
        }
        return null;
    }

    public int doInsert(Connection connection) {

        return 0;
    }
}
