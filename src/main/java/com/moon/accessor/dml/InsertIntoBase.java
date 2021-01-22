package com.moon.accessor.dml;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.util.List;

/**
 * @author benshaoye
 */
abstract class InsertIntoBase<R, TB extends Table<R, TB>> extends TableFieldsHolder<R, TB>
    implements InsertInto<R, TB> {

    private final DSLConfiguration config;
    private final TB table;

    @SafeVarargs
    InsertIntoBase(DSLConfiguration config, TB table, TableField<?, R, TB>... fields) {
        super(fields);
        this.config = config;
        this.table = table;
    }

    protected final DSLConfiguration getConfig() { return config; }

    public TB getTable() { return table; }

    private void doInsert(List<Object[]> values) {
        DSLConfiguration configuration = getConfig();
        configuration.runWithAutoRelease(connection -> {
            new Inserter<>(getTable(), getFields(), values).doInsert(connection);
        });
    }
}
