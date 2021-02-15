package com.moon.processing.decl;

import com.moon.accessor.annotation.TableFieldPolicy;

/**
 * @author benshaoye
 */
public class ColumnDeclared {

    private final TableFieldPolicy fieldPolicy;
    private final PropertyDeclared property;

    public ColumnDeclared(TableFieldPolicy fieldPolicy, PropertyDeclared property) {
        this.fieldPolicy = fieldPolicy;
        this.property = property;
    }

    public String getColumnName() {
        return null;
    }

    public boolean isWriteable() { return property.isWriteable(); }

    public boolean isReadable() { return property.isReadable(); }
}
