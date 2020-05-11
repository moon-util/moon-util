package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
class MarkExecutor {

    final static MarkExecutor NULL = new None();

    public static MarkExecutor of(
        MarkRenderer[] columns, MarkRenderer iterateAt
    ) { return of(columns, iterateAt, null); }

    public static MarkExecutor of(
        MarkRenderer[] columns, MarkRenderer iterateAt, MarkExecutor parent
    ) { return new MarkExecutor(columns, iterateAt, parent == null ? NULL : parent); }

    private final MarkExecutor parent;
    private final MarkRenderer[] columns;
    private final MarkRenderer iterateAt;
    private Object columnsData;
    private Object iterateData;

    private MarkExecutor(
        MarkRenderer[] columns, MarkRenderer iterateAt, MarkExecutor parent, boolean initialize
    ) {
        this.parent = initialize ? parent : requireNonNull(parent);
        this.columns = columns == null ? MarkRenderer.EMPTY : columns;
        this.iterateAt = iterateAt == null ? MarkRenderer.NONE : iterateAt;
    }

    private MarkExecutor(MarkRenderer[] columns, MarkRenderer iterateAt, MarkExecutor parent) {
        this(columns, iterateAt, parent, false);
    }

    void setIterateData(Object iterateData) { this.iterateData = iterateData; }

    void setColumnsData(Object columnsData) { this.columnsData = columnsData; }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private MarkRenderer[] getColumns() { return columns; }

    private Object getColumnsData() { return columnsData; }

    private Object getIterateData() { return iterateData; }

    void execute(RowFactory factory, Object data) {
        Object registriesData = getColumnsData();
        MarkRenderer[] registries = getColumns();
        for (MarkRenderer registry : registries) {
            registry.renderRecord(null, null, factory, registriesData);
        }
        iterateAt.renderRecord(null, null, factory, data);
    }

    private static class None extends MarkExecutor {

        public None() { super(null, null, null, true); }

        @Override
        void execute(RowFactory factory, Object data) { }
    }
}
