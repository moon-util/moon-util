package com.moon.notice;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author benshaoye
 */
public class NamedParameter implements Serializable, Cloneable, NoticeParameter {

    protected static final long serialVersionUID = 1L;

    private String name;
    private String value;

    public NamedParameter() { }

    public NamedParameter(String name, String value) {
        this.value = value;
        this.name = name;
    }

    public static NamedParameter of(String name, String value) { return new NamedParameter(name, value); }

    public String getName() { return name; }

    public NamedParameter setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() { return value; }

    public NamedParameter setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public NamedParameter clone() { return of(this.name, this.value); }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        NamedParameter that = (NamedParameter) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(name, value); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NamedParameter{");
        sb.append("name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String name() { return getName(); }

    @Override
    public String value() { return getValue(); }
}
