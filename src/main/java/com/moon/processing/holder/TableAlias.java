package com.moon.processing.holder;

/**
 * @author benshaoye
 */
public abstract class TableAlias {

    private final String group;
    private final String name;

    public TableAlias(String group, String name) {
        this.group = group;
        this.name = name;
    }

    public final String getAliasGroup() { return group; }

    public final String getAliasName() { return name; }
}
