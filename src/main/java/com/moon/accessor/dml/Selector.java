package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class Selector implements AliasCapable {

    private final boolean distinct;
    private final StringBuilder sql;

    public Selector(TableField<?, ?, ? extends Table<?, ?>>... fields) {
        this(false, fields);
    }

    public Selector(TableField<?, ?, ? extends Table<?, ?>> field) {
        this(false, field);
    }

    public Selector(boolean distinct, TableField<?, ?, ? extends Table<?, ?>>... fields) {
        this.distinct = distinct;
        Map<Object, String> tableName = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        for (TableField<?, ?, ? extends Table<?, ?>> field : fields) {
            // sql.append();
        }
        this.sql = sql;
    }

    public Selector(boolean distinct, TableField<?, ?, ? extends Table<?, ?>> field) {
        this.distinct = distinct;
        this.sql = new StringBuilder();
    }

    public Selector count(TableField<?, ?, ? extends Table<?, ?>> field) {
        return this;
    }

    public Selector countDistinct(TableField<?, ?, ? extends Table<?, ?>> field) {
        return this;
    }

    public Selector avg(TableField<?, ?, ? extends Table<?, ?>> field) {
        return this;
    }

    public Selector sum(TableField<?, ?, ? extends Table<?, ?>> field) {
        return this;
    }

    public Selector max(TableField<?, ?, ? extends Table<?, ?>> field) {
        return this;
    }

    public Selector min(TableField<?, ?, ? extends Table<?, ?>> field) {
        return this;
    }

    public SelectorFromClause from(Table<?, ?> table) {
        return new SelectorFromClause();
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
