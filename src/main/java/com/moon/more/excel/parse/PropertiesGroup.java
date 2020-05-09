package com.moon.more.excel.parse;

import java.io.Serializable;
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

    PropertiesGroup(List<T> columns, DetailRoot root, T rootProperty) {
        this.root = root == null ? DetailRoot.DEFAULT : root;
        this.columns = columns;
        this.rootProperty = rootProperty;

        // children maybe iterable
        boolean iterated = false;
        for (T column : columns) {
            if (column.isIterated()) {
                iterated = true;
                break;
            }
        }
        this.iterated = iterated;
    }

    public List<T> getColumns() { return dftIfNull(columns, emptyList()); }

    public T getRootProperty() { return rootProperty; }

    protected boolean hasStarting() { return getRootProperty() != null; }

    public int getMaxRowsLength() {
        return columns.stream().mapToInt(col -> {
            int len = col.getRowsLength();
            return len;
        }).max().orElse(0);
    }

    public boolean isIterated() { return iterated; }

    public void forEach(Consumer<T> consumer) { columns.forEach(consumer); }

    static PropertiesGroup<PropertyGet> ofGetter(
        List<PropertyGet> getters, DetailRoot root,//
        PropertyGet rootProperty
    ) { return new PropertiesGroupGet(getters, root, rootProperty); }

    static PropertiesGroup<PropertySet> ofSetter(
        List<PropertySet> setters, DetailRoot root,//
        PropertySet rootProperty
    ) { return new PropertiesGroupSet(setters, root, rootProperty); }
}
