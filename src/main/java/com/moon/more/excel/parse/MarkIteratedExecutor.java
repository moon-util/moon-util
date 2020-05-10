package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
class MarkIteratedExecutor {

    private final MarkIteratedExecutor parent;

    final static MarkIteratedExecutor NULL = new None();

    public static MarkIteratedExecutor of(MarkRenderer[] registries, MarkRenderer iterateAt) {
        return of(registries, iterateAt, null);
    }

    public static MarkIteratedExecutor of(
        MarkRenderer[] registries, MarkRenderer iterateAt, MarkIteratedExecutor parent
    ) {
        return new MarkIteratedExecutor(registries, iterateAt, parent == null ? NULL : parent);
    }

    private final MarkRenderer[] registries;
    private final MarkRenderer iterateAt;
    private Object registriesData;

    private MarkIteratedExecutor(
        MarkRenderer[] registries, MarkRenderer iterateAt, MarkIteratedExecutor parent, boolean initialize
    ) {
        this.parent = initialize ? parent : requireNonNull(parent);
        this.registries = registries == null ? MarkRenderer.EMPTY : registries;
        this.iterateAt = iterateAt == null ? MarkRenderer.NONE : iterateAt;
    }

    private MarkIteratedExecutor(MarkRenderer[] registries, MarkRenderer iterateAt, MarkIteratedExecutor parent) {
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

    private static class None extends MarkIteratedExecutor {

        public None() { super(null, null, null, true); }

        @Override
        void execute(RowFactory factory, Object data) { }
    }
}
