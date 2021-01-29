package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.Map;

/**
 * @author benshaoye
 */
abstract class TableUpdateFields<R, TB extends Table<R, TB>> extends TableHolder<R, TB>
    implements WhereClauseCapable<TableUpdateWhere<R, TB>> {

    /**
     * 各字段对应的值，目前有三种情况：
     * 1. 实际类型的值；
     * 2. null；
     * 3. 另一个字段。
     */
    private final Map<TableField<?, R, TB>, Object> setsMap;

    public TableUpdateFields(TB table, Map<TableField<?, R, TB>, Object> safeSetsMap) {
        super(table);
        this.setsMap = safeSetsMap;
    }

    protected Map<TableField<?, R, TB>, Object> getSetsMap() { return setsMap; }

    protected void afterSetsCalled() { this.setsMap.clear(); }

    @Override
    public TableUpdateWhere<R, TB> where(Conditional conditional) {
        TableUpdateWhere<R, TB> where = new TableUpdateWhereImpl<>(getTable(), getSetsMap(), conditional);
        afterSetsCalled();
        return where;
    }

    @Override
    public int done() {
        return 0;
    }
}
