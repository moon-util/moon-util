package com.moon.poi.excel.table;

import com.moon.core.util.ListUtil;
import com.moon.poi.excel.annotation.SheetColumn;
import com.moon.poi.excel.annotation.SheetColumnGroup;
import com.moon.poi.excel.annotation.style.DefinitionStyle;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author moonsky
 */
final class Marked<T extends Member> implements Descriptor {

    protected final static <M extends AnnotatedElement, T extends Annotation> T obtain(M m, Class<T> type) {
        return m.getAnnotation(type);
    }

    protected final static <T extends Annotation> T obtain(Member m, Class<T> type) {
        return obtain((AnnotatedElement) m, type);
    }

    static Marked of(Field field) { return new Marked(field.getName(), field.getType(), field); }

    static Marked of(PropertyDescriptor descriptor, Method method) {
        return new Marked(descriptor.getName(), descriptor.getPropertyType(), method);
    }

    private final static short[] DEFAULT_HEIGHT_ARR = {};

    private final String name;
    private final Class type;
    private final T member;
    private final SheetColumn annotationColumn;
    private final SheetColumnGroup annotationGroup;

    protected Marked(String name, Class type, T member) {
        this.member = Objects.requireNonNull(member);
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);

        this.annotationColumn = obtain(member, SheetColumn.class);
        this.annotationGroup = obtain(member, SheetColumnGroup.class);
        // Assert.notDuplicated(this);
    }

    public T getMember() { return member; }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return obtain(member, annotationType);
    }

    @Override
    public List<DefinitionStyle> getDefinitionStylesOnMethod() {
        if (member instanceof Method) {
            return ListUtil.newList(((Method) member).getAnnotationsByType(DefinitionStyle.class));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<DefinitionStyle> getDefinitionStylesOnField() {
        if (member instanceof Field) {
            return ListUtil.newList(((Field) member).getAnnotationsByType(DefinitionStyle.class));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public SheetColumn getTableColumn() { return annotationColumn; }

    @Override
    public SheetColumnGroup getTableColumnGroup() { return annotationGroup; }

    @Override
    public String[] getTitles() {
        return getOrDefault(col -> col.value(), null);
    }

    @Override
    public short[] getHeadHeightArr() {
        return getOrDefault(col -> col.rowsHeight4Head(), DEFAULT_HEIGHT_ARR);
    }

    @Override
    public Integer getColumnWidth() { return isAnnotated() ? getTableColumn().width() : null; }

    @Override
    public String getName() { return name; }

    @Override
    public Class getPropertyType() { return type; }
}
