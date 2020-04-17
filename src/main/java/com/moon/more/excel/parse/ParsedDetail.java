package com.moon.more.excel.parse;

import java.io.Serializable;
import java.util.List;

import static com.moon.more.excel.parse.ParamUtil.dftIfNull;
import static java.util.Collections.emptyList;

/**
 * @author benshaoye
 */
abstract class ParsedDetail<T extends Defined> implements Serializable {

    protected final List<T> columns;
    protected final RootDetail root;
    protected final T starting;
    protected final T ending;

    ParsedDetail(List<T> columns, RootDetail root, T starting, T ending) {
        this.root = root == null ? RootDetail.DEFAULT : root;
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

    static ParsedDetail<DefinedGet> ofGetter(
        List<DefinedGet> getters, RootDetail root,//
        DefinedGet starting, DefinedGet ending
    ) { return new ParsedGetDetail(getters, root, starting, ending); }

    static ParsedDetail<DefinedSet> ofSetter(
        List<DefinedSet> setters, RootDetail root,//
        DefinedSet starting, DefinedSet ending
    ) { return new ParsedSetDetail(setters, root, starting, ending); }
}
