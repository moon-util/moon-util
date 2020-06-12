package com.moon.more.excel.table;

import com.moon.more.excel.PropertyControl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author benshaoye
 */
class Attribute implements Descriptor {

    private final Marked onField;
    private final Marked onMethod;

    Attribute(Marked onMethod, Marked onField) {
        this.onMethod = onMethod;
        this.onField = onField;
    }

    private final <T> T obtain(Function<Marked, T> getter) {
        Marked marked = this.onMethod;
        if (marked != null) {
            return getter.apply(marked);
        }
        return getter.apply(this.onField);
    }

    public PropertyControl getValueGetter() {
        Method method = this.onMethod == null ? null : (Method) this.onMethod.getMember();
        Field field = this.onField == null ? null : (Field) this.onField.getMember();
        return ValueGetter.of(method, field);
    }

    @Override
    public String getName() {
        return obtain(m -> m.getName());
    }

    @Override
    public Class getPropertyType() {
        return obtain(m -> m.getPropertyType());
    }
}
