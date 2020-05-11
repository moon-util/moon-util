package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
class MarkExecutor {

    final static MarkExecutor NULL = new None();

    public static MarkExecutor of(MarkRenderer[] registries, MarkRenderer iterateAt) {
        return of(registries, iterateAt, null);
    }

    public static MarkExecutor of(
        MarkRenderer[] registries, MarkRenderer iterateAt, MarkExecutor parent
    ) { return new MarkExecutor(registries, iterateAt, parent == null ? NULL : parent); }

    private final MarkExecutor parent;
    private final MarkRenderer[] registries;
    private final MarkRenderer iterateAt;
    private Object registriesData;

    private MarkExecutor(
        MarkRenderer[] registries, MarkRenderer iterateAt, MarkExecutor parent, boolean initialize
    ) {
        this.parent = initialize ? parent : requireNonNull(parent);
        this.registries = registries == null ? MarkRenderer.EMPTY : registries;
        this.iterateAt = iterateAt == null ? MarkRenderer.NONE : iterateAt;
    }

    private MarkExecutor(MarkRenderer[] registries, MarkRenderer iterateAt, MarkExecutor parent) {
        this(registries, iterateAt, parent, false);
    }

    public void setRegistriesData(Object data) { this.registriesData = data; }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private MarkRenderer[] getRegistries() { return registries; }

    private Object getRegistriesData() { return registriesData; }

    void execute(RowFactory factory, Object data) {
        Object registriesData = getRegistriesData();
        MarkRenderer[] registries = getRegistries();
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
