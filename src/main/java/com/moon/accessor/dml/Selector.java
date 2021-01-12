package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class Selector<R> implements WhereConditional<SelectorWhereClause>, AliasCapable {

    private final boolean distinct;
    private final StringBuilder sql;

    public Selector(Field<?, ?, ? extends Table<?>>... fields) {
        this(false, fields);
    }

    public Selector(Field<?, ?, ? extends Table<?>> field) {
        this(false, field);
    }

    public Selector(boolean distinct, Field<?, ?, ? extends Table<?>>... fields) {
        this.distinct = distinct;
        Map<Object, String> tableName = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        for (Field<?, ?, ? extends Table<?>> field : fields) {
            // sql.append();
        }
        this.sql = sql;
    }

    public Selector(boolean distinct, Field<?, ?, ? extends Table<?>> field) {
        this.distinct = distinct;
        this.sql = new StringBuilder();
    }

    public Selector<R> count(Field<?, ?, ? extends Table<?>> field) {
        return this;
    }

    public Selector<R> max(Field<?, ?, ? extends Table<?>> field) {
        return this;
    }

    public Selector<R> min(Field<?, ?, ? extends Table<?>> field) {
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
