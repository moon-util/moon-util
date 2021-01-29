package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class TableUpdateWhereImpl<R, TB extends Table<R, TB>> extends TableHolder<R, TB>
    implements TableUpdateWhere<R, TB> {

    private final Map<TableField<?, R, TB>, Object> fieldsSetsMap = new LinkedHashMap<>();

    public TableUpdateWhereImpl(TB table, Map<TableField<?, R, TB>, Object> fieldsSetsMap, Conditional conditional) {
        super(table);
        this.fieldsSetsMap.putAll(fieldsSetsMap);
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
