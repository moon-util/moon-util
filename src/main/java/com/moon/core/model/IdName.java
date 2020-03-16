package com.moon.core.model;

import com.moon.core.enums.EnumDescriptor;
import com.moon.core.getter.IdGetter;
import com.moon.core.getter.NameGetter;
import com.moon.core.util.interfaces.IdOperator;
import com.moon.core.util.interfaces.IdSupplier;
import com.moon.core.util.interfaces.NameSupplier;

import java.util.Objects;

/**
 * @author benshaoye
 */
public final class IdName
    implements NameSupplier<String>, IdOperator<String>,
               IdSupplier<String>, IdGetter, NameGetter {

    private String id;

    private String name;

    public IdName() { }

    public static IdName of() {return new IdName();}

    public IdName(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public static IdName of(String id, String name) { return new IdName(id, name); }

    public static <T extends Enum<T>> IdName of(T enumItem) {
        String id, name;
        if (enumItem instanceof EnumDescriptor) {
            EnumDescriptor item = (EnumDescriptor) enumItem;
            id = item.getName();
            name = item.getText();
        } else {
            id = enumItem.name();
            name = enumItem.name();
        }
        return of(id, name);
    }

    public static IdName of(KeyValue keyValue) {
        return keyValue == null ? of() : of(keyValue.getKey(), keyValue.getValue());
    }

    public KeyValue toKeyValue() { return KeyValue.of(this); }

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdName idName = (IdName) o;
        return Objects.equals(id, idName.id) && Objects.equals(name, idName.name);
    }

    @Override
    public int hashCode() { return Objects.hash(id, name); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('{');
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
