package com.moon.processing.defaults;

import com.moon.accessor.annotation.TableModel;

/**
 * @author benshaoye
 */
public class TableModelEnum implements TableModel {

    /** default instance */
    public static final TableModelEnum INSTANCE = new TableModelEnum();

    private TableModelEnum() { }

    @Override
    public String name() { return ""; }

    @Override
    public String alias() { return ""; }

    @Override
    public Class<TableModel> annotationType() { return TableModel.class; }
}
