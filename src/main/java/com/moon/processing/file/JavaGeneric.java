package com.moon.processing.file;

import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import java.util.Objects;

/**
 * @author benshaoye
 */
public class JavaGeneric {

    private final Importer importer;
    private final String name;
    private GenericType type;
    private String bound;

    public JavaGeneric(Importer importer, String name) {
        this.importer = importer;
        this.name = name;
    }

    public JavaGeneric superOf(String bound) {
        this.type = GenericType.SUPER;
        this.bound = bound;
        return this;
    }

    public JavaGeneric extendsOf(String bound) {
        this.type = GenericType.EXTENDS;
        this.bound = bound;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        JavaGeneric that = (JavaGeneric) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    /**
     * 返回单个的泛型声明，如果包含边界，返回的结果也包含边界
     *
     * @return stringify
     */
    @Override
    public String toString() {
        return String2.isBlank(bound) ? name : String.join(" ", name, type.name(), importer.onImported(bound));
    }

    private enum GenericType {
        /** types */
        SUPER,
        EXTENDS
    }
}
