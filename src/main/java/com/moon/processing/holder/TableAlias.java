package com.moon.processing.holder;

/**
 * @author benshaoye
 */
public class TableAlias {

    public final static TableAlias NULLS = new NullAlias();

    private final String group;
    private final String name;

    protected TableAlias(TableAlias alias) { this(alias.group, alias.name); }

    public TableAlias(String group, String name) {
        this.group = group;
        this.name = name;
    }

    public final String getAliasGroup() { return group; }

    public final String getAliasName() { return name; }

    public String toAliasRef(String name) {
        return group + '.' + this.name + '.' + name;
    }

    private final static class NullAlias extends TableAlias {

        public NullAlias() { super(null, null); }

        @Override
        public String toAliasRef(String name) { return null; }
    }
}
