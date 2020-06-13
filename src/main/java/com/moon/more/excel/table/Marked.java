package com.moon.more.excel.table;

import com.moon.more.excel.annotation.TableColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Objects;

/**
 * @author benshaoye
 */
abstract class Marked<T extends Member> implements Descriptor {

    private static <M extends AnnotatedElement, T extends Annotation> T obtain(M m, Class<T> type) {
        return m.getAnnotation(type);
    }

    private final String name;
    private final Class type;
    private final T member;
    private final TableColumn annotationCol;

    protected Marked(String name, Class type, T member) {
        this.member = Objects.requireNonNull(member);
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);

        AnnotatedElement elem = (AnnotatedElement) member;
        this.annotationCol = obtain(elem, TableColumn.class);
    }

    public boolean isAnnotatedCol() { return annotationCol != null; }

    public T getMember() { return member; }

    @Override
    public TableColumn getTableColumn() { return annotationCol; }

    @Override
    public String[] getTitles() {
        return isAnnotatedCol() ? annotationCol.value() : null;
    }

    @Override
    public String getName() { return name; }

    @Override
    public Class getPropertyType() { return type; }
}
