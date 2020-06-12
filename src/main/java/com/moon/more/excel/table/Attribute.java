package com.moon.more.excel.table;

import java.util.function.Function;

/**
 * @author benshaoye
 */
class Attribute implements Descriptor {

    private final Marked onField;
    private final Marked onMethod;

    Attribute(Marked onMethod, Marked onField) {
        this.onMethod = onMethod;
        this.onField = onField;
    }

    private final <T> T obtain(Function<Marked, T> getter) {
        Marked marked = this.onMethod;
        if (marked != null) {
            return getter.apply(marked);
        }
        return getter.apply(this.onField);
    }

    @Override
    public String getName() {
        return obtain(m -> m.getName());
    }

    @Override
    public Class getPropertyType() {
        return obtain(m -> m.getPropertyType());
    }
}
