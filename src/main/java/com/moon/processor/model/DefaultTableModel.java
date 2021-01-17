package com.moon.processor.model;

import com.moon.accessor.annotation.TableEntity;

/**
 * @author benshaoye
 */
public class DefaultTableModel implements TableEntity {

    /** default instance */
    public static final DefaultTableModel INSTANCE = new DefaultTableModel();

    private DefaultTableModel() { }

    @Override
    public String name() { return ""; }

    @Override
    public String alias() { return ""; }

    @Override
    public Class<TableEntity> annotationType() { return TableEntity.class; }
}
