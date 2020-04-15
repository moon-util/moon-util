package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import com.moon.more.excel.annotation.DataIndexer;
import com.moon.more.excel.annotation.DataListable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * @author benshaoye
 */
class Marked<M extends Member> {

    private static <M extends AnnotatedElement, T extends Annotation> T obtain(M m, Class<T> type) {
        return m.getAnnotation(type);
    }

    private final String name;
    private final M member;
    private final Class propertyType;
    private final Type genericType;
    private final boolean canListable;
    private final DataColumn column;
    private final DataColumnFlatten flatten;
    private final DataIndexer indexer;
    private final DataListable listable;
    private final ParsedDetail children;

    Marked(String name, Class propertyType, Type genericType, M member, ParsedDetail children) {
        this.name = name;
        this.member = member;
        this.genericType = genericType;
        this.propertyType = propertyType;
        this.children = children;
        AnnotatedElement elem = (AnnotatedElement) member;
        this.column = obtain(elem, DataColumn.class);
        this.flatten = obtain(elem, DataColumnFlatten.class);
        this.indexer = obtain(elem, DataIndexer.class);
        this.listable = obtain(elem, DataListable.class);
        ParamUtil.requireNotDuplicated(column, flatten, name);
        this.canListable = BaseParser.isSetColumn(propertyType);
    }

    public String[] getHeadLabels() {
        return flatten == null ? (column == null ? null : column.value()) : flatten.value();
    }

    public String getHeadLabelAsIndexer() {
        return getIndexer().value();
    }

    boolean isAnnotated() { return column != null || flatten != null || indexer != null; }

    boolean isDefined() { return getFlatten() != null || getColumn() != null; }

    boolean isIndexer() {
        return getIndexer() != null;
    }

    public String getName() { return name; }

    public M getMember() { return member; }

    public Class<?> getPropertyType() { return propertyType; }

    public DataColumn getColumn() { return column; }

    public DataColumnFlatten getFlatten() { return flatten; }

    public DataIndexer getIndexer() { return indexer; }

    public DataListable getListable() { return listable; }

    public Type getGenericType() { return genericType; }

    public boolean isCanListable() { return canListable; }

    public ParsedDetail getChildren() { return children; }

    static Marked<Field> of(Field member, ParsedDetail children) {
        return new Marked<>(member.getName(), member.getType(),//
            member.getGenericType(), member, children);
    }

    static Marked<Method> of(String name, Class propertyType, Type genericType, Method method, ParsedDetail children) {
        return new Marked<>(name, propertyType, genericType, method, children);
    }
}


