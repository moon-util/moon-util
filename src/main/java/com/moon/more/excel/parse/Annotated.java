package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.annotation.TableListable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * @author benshaoye
 */
class Annotated<M extends Member> {

    private static <M extends AnnotatedElement, T extends Annotation> T obtain(M m, Class<T> type) {
        return m.getAnnotation(type);
    }

    private final String name;
    private final M member;
    private final Class propertyType;
    private final Type genericType;
    private final Class actualClass;
    private final boolean iterated;
    private final TableColumn column;
    private final TableColumnFlatten flatten;
    private final TableIndexer indexer;
    private final TableListable listable;
    private final PropertiesGroup children;

    Annotated(
        String name, Class propertyType, Type genericType, Class actualClass, M member, PropertiesGroup children
    ) {
        this.name = name;
        this.member = member;
        this.actualClass = actualClass;
        this.genericType = genericType;
        this.propertyType = propertyType;
        this.children = children;
        AnnotatedElement elem = (AnnotatedElement) member;
        this.column = obtain(elem, TableColumn.class);
        this.flatten = obtain(elem, TableColumnFlatten.class);
        this.indexer = obtain(elem, TableIndexer.class);
        this.listable = obtain(elem, TableListable.class);
        SupportUtil.requireNotDuplicated(column, flatten, name);
        this.iterated = AbstractSupporter.isSetColumn(propertyType);
    }

    public String[] getHeadLabels() {
        return flatten == null ? (column == null ? null : column.value()) : flatten.value();
    }

    public String getHeadLabelAsIndexer() { return getIndexer().value(); }

    boolean isAnnotated() { return column != null || flatten != null || indexer != null; }

    boolean isDefined() { return getFlatten() != null || getColumn() != null; }

    boolean isIndexer() { return getIndexer() != null; }

    public String getName() { return name; }

    public M getMember() { return member; }

    public Class<?> getPropertyType() { return propertyType; }

    public TableColumn getColumn() { return column; }

    public TableColumnFlatten getFlatten() { return flatten; }

    public TableIndexer getIndexer() { return indexer; }

    public TableListable getListable() { return listable; }

    public Type getGenericType() { return genericType; }

    public Class getActualClass() { return actualClass; }

    public boolean isIterated() { return iterated; }

    public PropertiesGroup getChildren() { return children; }

    @SuppressWarnings("all")
    static Annotated<Field> of(Field member, Class actualClass, PropertiesGroup children) {
        return new Annotated<>(member.getName(), member.getType(),//
            member.getGenericType(), actualClass, member, children);
    }

    @SuppressWarnings("all")
    static Annotated<Method> of(
        String name, Class propertyType, Type genericType,//
        Class actualClass, Method method, PropertiesGroup children
    ) { return new Annotated<>(name, propertyType, genericType, actualClass, method, children); }
}


