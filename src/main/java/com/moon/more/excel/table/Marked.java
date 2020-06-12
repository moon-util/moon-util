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
    private final TableColumn annoCol;

    protected Marked(String name, Class type, T member) {
        this.member = Objects.requireNonNull(member);
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);

        AnnotatedElement elem = (AnnotatedElement) member;
        this.annoCol = obtain(elem, TableColumn.class);
    }

    public boolean isAnnotatedCol() {
        return annoCol != null;
    }

    public T getMember() { return member; }

    // protected abstract Operation getOperation();

    @Override
    public String getName() { return name; }

    @Override
    public Class getPropertyType() { return type; }
}
