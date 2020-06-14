package com.moon.more.excel.table;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Objects;

/**
 * @author benshaoye
 */
abstract class Marked<T extends Member> implements Descriptor {

    private static <T extends Annotation> T obtain(Member m, Class<T> type) {
        return obtain((AnnotatedElement) m, type);
    }

    private static <M extends AnnotatedElement, T extends Annotation> T obtain(M m, Class<T> type) {
        return m.getAnnotation(type);
    }

    private final String name;
    private final Class type;
    private final T member;
    private final TableColumn annotationColumn;
    private final TableColumnGroup annotationGroup;

    protected Marked(String name, Class type, T member) {
        this.member = Objects.requireNonNull(member);
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);

        this.annotationColumn = obtain(member, TableColumn.class);
        this.annotationGroup = obtain(member, TableColumnGroup.class);
        Assert.notDuplicated(this);
    }

    public T getMember() { return member; }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return obtain(member, annotationType);
    }

    @Override
    public TableColumn getTableColumn() { return annotationColumn; }

    @Override
    public TableColumnGroup getTableColumnGroup() { return annotationGroup; }

    @Override
    public String[] getTitles() {
        if (isAnnotatedColumn()) {
            return getTableColumn().value();
        }
        if (isAnnotatedGroup()) {
            return getTableColumnGroup().value();
        }
        return null;
    }

    @Override
    public String getName() { return name; }

    @Override
    public Class getPropertyType() { return type; }
}
