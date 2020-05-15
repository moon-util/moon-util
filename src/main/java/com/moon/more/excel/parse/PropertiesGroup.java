package com.moon.more.excel.parse;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static com.moon.more.excel.parse.SupportUtil.dftIfNull;
import static java.util.Collections.emptyList;

/**
 * @author benshaoye
 */
abstract class PropertiesGroup<T extends Property> implements Serializable {

    protected final List<T> columns;
    protected final DetailRoot root;
    protected final T rootProperty;
    protected final boolean iterated;
    protected final boolean childrenHasIterated;

    PropertiesGroup(List<T> columns, DetailRoot root, T rootProperty) {
        this.root = root == null ? DetailRoot.DEFAULT : root;
        this.columns = columns;
        this.rootProperty = rootProperty;

        // children maybe iterable
        this.iterated = hasIterated(columns);
        this.childrenHasIterated = hasChildrenIterated(columns);
    }

    private static <T extends Property> boolean hasIterated(Collection<T> columns) {
        for (T column : columns) {
            if (column.isIterated()) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Property> boolean hasChildrenIterated(List<T> columns){
        for (T column : columns) {
            if (column.isIterated() || column.isChildrenIterated()) {
                return true;
            }
        }
        return false;
    }

    public List<T> getColumns() { return dftIfNull(columns, emptyList()); }

    public T getRootProperty() { return rootProperty; }

    protected boolean hasStarting() { return getRootProperty() != null; }

    public boolean isIterated() { return iterated; }

    public boolean isChildrenIterated() { return childrenHasIterated; }

    static PropertiesGroup<PropertyGet> ofGetter(
        List<PropertyGet> getters, DetailRoot root,//
        PropertyGet rootProperty
    ) { return new PropertiesGroupGet(getters, root, rootProperty); }

    static PropertiesGroup<PropertySet> ofSetter(
        List<PropertySet> setters, DetailRoot root,//
        PropertySet rootProperty
    ) { return new PropertiesGroupSet(setters, root, rootProperty); }
}
