package com.moon.more.excel.parse;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

import static com.moon.more.excel.parse.ParamUtil.dftIfNull;
import static java.util.Collections.emptyList;

/**
 * @author benshaoye
 */
abstract class Detail<T extends Defined> implements Serializable {

    protected final List<T> columns;
    protected final DetailRoot root;
    protected final T starting;
    protected final T ending;

    Detail(List<T> columns, DetailRoot root, T starting, T ending) {
        this.root = root == null ? DetailRoot.DEFAULT : root;
        this.columns = columns;
        this.starting = starting;
        this.ending = ending;
    }

    public List<T> getColumns() { return dftIfNull(columns, emptyList()); }

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

    public void forEach(Consumer<T> consumer) {
        columns.forEach(consumer);
    }

    static Detail<DefinedGet> ofGetter(
        List<DefinedGet> getters, DetailRoot root,//
        DefinedGet starting, DefinedGet ending
    ) { return new DetailGet(getters, root, starting, ending); }

    static Detail<DefinedSet> ofSetter(
        List<DefinedSet> setters, DetailRoot root,//
        DefinedSet starting, DefinedSet ending
    ) { return new DetailSet(setters, root, starting, ending); }
}
