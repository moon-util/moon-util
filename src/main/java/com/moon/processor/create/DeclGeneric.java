package com.moon.processor.create;

import com.moon.processor.holder.Importer;

import java.util.Objects;

/**
 * @author benshaoye
 */
public class DeclGeneric {

    private final Importer importer;
    private final String name;
    private GenericType type;
    private String bound;

    public DeclGeneric(Importer importer, String name) {
        this.importer = importer;
        this.name = name;
    }

    public DeclGeneric superOf(String bound) {
        this.type = GenericType.SUPER;
        this.bound = bound;
        return this;
    }

    public DeclGeneric extendsOf(String bound) {
        this.type = GenericType.EXTENDS;
        this.bound = bound;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        DeclGeneric that = (DeclGeneric) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() {
        return bound == null ? name : String.join(" ", name, type.name(), importer.onImported(bound));
    }

    private enum GenericType {
        SUPER,
        EXTENDS
    }
}
