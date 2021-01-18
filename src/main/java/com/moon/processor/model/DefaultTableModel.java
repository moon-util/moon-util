package com.moon.processor.model;

import com.moon.accessor.annotation.TableModel;

/**
 * @author benshaoye
 */
public class DefaultTableModel implements TableModel {

    /** default instance */
    public static final DefaultTableModel INSTANCE = new DefaultTableModel();

    private DefaultTableModel() { }

    @Override
    public String name() { return ""; }

    @Override
    public String alias() { return ""; }

    @Override
    public Class<TableModel> annotationType() { return TableModel.class; }
}
