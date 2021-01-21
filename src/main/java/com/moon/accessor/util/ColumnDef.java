package com.moon.accessor.util;

/**
 * @author benshaoye
 */
public final class ColumnDef {

    /**
     * 列名，可能是被反引号包裹的，如: `username`
     */
    private final String column;
    /**
     * 列名，未被反引号包裹的，如:  username
     */
    private final String name;

    public ColumnDef(String column, String name) {
        this.column = column;
        this.name = name;
    }
}
