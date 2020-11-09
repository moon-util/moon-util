package com.moon.mapping.processing;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
final class StringAdder implements Supplier<String> {

    private final StringBuilder content;

    public StringAdder() { this.content = new StringBuilder(); }

    public StringAdder add(Object obj) { return add(obj == null ? null : obj.toString()); }

    public StringAdder add(CharSequence content) {
        this.content.append(content);
        return this;
    }

    public StringAdder addSpace(){
        return add(" ");
    }

    @Override
    public String toString() { return content.toString(); }

    @Override
    public String get() { return content.toString(); }
}
