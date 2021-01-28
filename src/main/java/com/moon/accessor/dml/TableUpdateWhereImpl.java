package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public class TableUpdateWhereImpl<R, TB extends Table<R, TB>> extends TableUpdateBase<R, TB>
    implements TableUpdateWhere<R, TB> {

    private final TableField<?, R, TB>[] fields;
    private final Object[] values;

    public TableUpdateWhereImpl(TB table, TableField<?, R, TB>[] fields, Object[] values, Conditional conditional) {
        super(table);
        this.fields = fields;
        this.values = values;
    }

    @Override
    public TableUpdateWhere<R, TB> and(Conditional conditional) {
        return this;
    }

    @Override
    public TableUpdateWhere<R, TB> or(Conditional conditional) {
        return this;
    }

    @Override
    public int done() {
        return 0;
    }
}
