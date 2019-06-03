package com.moon.notice;

import java.util.Objects;

/**
 * @author benshaoye
 */
public class IndexedParameter extends NamedParameter {

    public IndexedParameter() { super(); }

    public IndexedParameter(String name, String value) { super(name, value); }

    public static IndexedParameter of(String name, String value) { return new IndexedParameter(name, value); }

    @Override
    public IndexedParameter setName(String name) {
        super.setName(name);
        return this;
    }

    @Override
    public IndexedParameter setValue(String value) {
        super.setValue(value);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        IndexedParameter that = (IndexedParameter) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndexedParameter{");
        sb.append("index='").append(getName()).append('\'');
        sb.append(", value='").append(getValue()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public IndexedParameter clone() { return of(getName(), getValue()); }
}
