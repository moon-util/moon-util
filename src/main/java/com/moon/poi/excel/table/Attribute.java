package com.moon.poi.excel.table;

import com.moon.core.lang.ArrayUtil;
import com.moon.poi.excel.PropertyControl;
import com.moon.poi.excel.annotation.SheetColumn;
import com.moon.poi.excel.annotation.SheetColumnGroup;
import com.moon.poi.excel.annotation.style.DefinitionStyle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author moonsky
 */
final class Attribute implements Descriptor, Comparable<Attribute> {

    private final Marked onField;
    private final Marked onMethod;

    Attribute(Marked onMethod, Marked onField) {
        this.onMethod = onMethod;
        this.onField = onField;
        Assert.notDuplicated(onMethod, onField);
    }

    private <T> T obtainOrNull(Marked marked, Function<Marked, T> getter) {
        return marked == null ? null : getter.apply(marked);
    }

    // private <T> T obtain(Predicate<? super T> tester, Function<Marked, T> getter) {
    //     T value = obtainOrNull(onMethod, getter);
    //     return tester.test(value) ? obtainOrNull(onField, getter) : value;
    // }

    private <T> T obtainOrNull(Function<Marked, T> getter) {
        T value = obtainOrNull(onMethod, getter);
        return value == null ? obtainOrNull(onField, getter) : value;
    }

    public PropertyControl getValueGetter() {
        Method method = this.onMethod == null ? null : (Method) this.onMethod.getMember();
        Field field = this.onField == null ? null : (Field) this.onField.getMember();
        return ValueGetter.of(method, field);
    }

    public TransferForGet getTransformOrDefault() {
        return TransferForGet.findOrDefault(getPropertyType());
    }

    @Override
    public String[] getTitles() {
        String[] titles = obtainOrNull(m -> m.getTitles());
        return titles == null ? ArrayUtil.toArray(getName()) : titles;
    }

    @Override
    public short[] getHeadHeightArr() {
        return obtainOrNull(m -> m.getHeadHeightArr());
    }

    @Override
    public Integer getColumnWidth() {
        SheetColumn column = getTableColumn();
        return column == null ? null : column.width() < 0 ? null : column.width();
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
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return (T) obtainOrNull(m -> m.getAnnotation(annotationType));
    }

    Method getMemberMethod() {
        return (Method) onMethod.getMember();
    }

    Field getMemberField() {
        return (Field) onField.getMember();
    }

    @Override
    public List<DefinitionStyle> getDefinitionStylesOnMethod() {
        return onMethod == null ? Collections.EMPTY_LIST : onMethod.getDefinitionStylesOnMethod();
    }

    @Override
    public List<DefinitionStyle> getDefinitionStylesOnField() {
        return onField == null ? Collections.EMPTY_LIST : onField.getDefinitionStylesOnField();
    }

    @Override
    public SheetColumn getTableColumn() {
        return obtainOrNull(m -> m.getTableColumn());
    }

    @Override
    public SheetColumnGroup getTableColumnGroup() {
        return obtainOrNull(m -> m.getTableColumnGroup());
    }

    public int getOrder() {
        return getOrDefault(SheetColumn::order, 0);
    }

    @Override
    public int compareTo(Attribute o) { return this.getOrder() - o.getOrder(); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Attribute{");
        sb.append("name=").append(getName());
        sb.append(", onField=").append(onField);
        sb.append(", onMethod=").append(onMethod);
        sb.append('}');
        return sb.toString();
    }
}
