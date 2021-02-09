package com.moon.processing.decl;

/**
 * @author benshaoye
 */
public class ColumnDeclared {

    private final PropertyDeclared property;

    public ColumnDeclared(PropertyDeclared property) {
        this.property = property;
    }

    public boolean isWriteable() { return property.isWriteable(); }

    public boolean isReadable() { return property.isReadable(); }
}
