package com.moon.more.excel.parse;

import com.moon.core.util.ListUtil;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static com.moon.more.excel.parse.SupportUtil.dftIfNull;
import static java.util.Collections.emptyList;

/**
 * @author benshaoye
 */
public abstract class PropertiesGroup<T extends Property> implements Serializable {

    protected final List<T> columns;
    protected final DetailRoot root;
    protected final T starting;
    protected final T ending;

    PropertiesGroup(List<T> columns, DetailRoot root, T starting, T ending) {
        this.root = root == null ? DetailRoot.DEFAULT : root;
        this.columns = columns;
        this.starting = starting;
        this.ending = ending;
    }

    public List<T> getColumns() { return dftIfNull(columns, emptyList()); }

    public T[] getColumnsArr() { return ListUtil.toArray(getColumns(), getArrCreator()); }

    protected IntFunction<T[]> getArrCreator() {
        throw new UnsupportedOperationException();
    }

    public T getStarting() { return starting; }

    protected boolean hasStarting() { return getStarting() != null; }

    public T getEnding() { return ending; }

    protected boolean hasEnding() { return getEnding() != null; }

    public int getMaxRowsLength() {
        return columns.stream().mapToInt(col -> {
            int len = col.getRowsLength();
            return len;
        }).max().orElse(0);
    }

    public void forEach(Consumer<T> consumer) { columns.forEach(consumer); }

    static PropertiesGroup<PropertyGet> ofGetter(
        List<PropertyGet> getters, DetailRoot root,//
        PropertyGet starting, PropertyGet ending
    ) { return new PropertiesGroupGet(getters, root, starting, ending); }

    static PropertiesGroup<PropertySet> ofSetter(
        List<PropertySet> setters, DetailRoot root,//
        PropertySet starting, PropertySet ending
    ) { return new PropertiesGroupSet(setters, root, starting, ending); }
}
