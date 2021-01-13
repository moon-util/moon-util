package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.TableField;
import com.moon.accessor.meta.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class Selector<R> implements WhereConditional<SelectorWhereClause>, AliasCapable {

    private final boolean distinct;
    private final StringBuilder sql;

    public Selector(TableField<?, ?, ? extends Table<?>>... fields) {
        this(false, fields);
    }

    public Selector(TableField<?, ?, ? extends Table<?>> field) {
        this(false, field);
    }

    public Selector(boolean distinct, TableField<?, ?, ? extends Table<?>>... fields) {
        this.distinct = distinct;
        Map<Object, String> tableName = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        for (TableField<?, ?, ? extends Table<?>> field : fields) {
            // sql.append();
        }
        this.sql = sql;
    }

    public Selector(boolean distinct, TableField<?, ?, ? extends Table<?>> field) {
        this.distinct = distinct;
        this.sql = new StringBuilder();
    }

    public Selector<R> count(TableField<?, ?, ? extends Table<?>> field) {
        return this;
    }

    public Selector<R> max(TableField<?, ?, ? extends Table<?>> field) {
        return this;
    }

    public Selector<R> min(TableField<?, ?, ? extends Table<?>> field) {
        return this;
    }

    public Selector<R> from(Table<R> table) {
        return this;
    }

    @Override
    public SelectorWhereClause where() {
        return null;
    }

    @Override
    public SelectorWhereClause where(Conditional condition) {
        return null;
    }

    @Override
    public Object as(String alias) {
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
