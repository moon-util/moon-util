package com.moon.more.excel.table;

import com.moon.core.lang.ArrayUtil;
import com.moon.more.excel.PropertyControl;
import com.moon.more.excel.annotation.TableColumn;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    private final <T> T obtainOrNull(Function<Marked, T> getter) {
        Marked marked = this.onMethod;
        if (marked != null) {
            return getter.apply(marked);
        }
        marked = this.onField;
        if (marked != null) {
            return getter.apply(this.onField);
        }
        return null;
    }

    public PropertyControl getValueGetter() {
        Method method = this.onMethod == null ? null : (Method) this.onMethod.getMember();
        Field field = this.onField == null ? null : (Field) this.onField.getMember();
        return ValueGetter.of(method, field);
    }

    public TransformForGet getTransformForGet(){
        return TransformForGet.findOrDefault(getPropertyType());
    }

    @Override
    public String[] getTitles() {
        String[] titles = obtainOrNull(m -> m.getTitles());
        return titles == null ? ArrayUtil.toArray(getName()) : titles;
    }

    @Override
    public String getName() {
        return obtainOrNull(m -> m.getName());
    }

    @Override
    public Class getPropertyType() {
        return obtainOrNull(m -> m.getPropertyType());
    }

    @Override
    public TableColumn getTableColumn() {
        return obtainOrNull(m -> m.getTableColumn());
    }
}
