package com.moon.mapping.processing;

import java.util.Arrays;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;

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

    public StringAdder add(boolean doAdd, CharSequence content) {
        return doAdd ? add(content) : this;
    }

    public StringAdder implement(Class... interfaces) {
        add(" implements ");
        return add(Arrays.stream(interfaces).map(Class::getCanonicalName).collect(joining(",")));
    }

    public StringAdder implement(String... interfaces) {
        add(" implements ");
        return add(Arrays.stream(interfaces).collect(joining(",")));
    }

    public StringAdder space() { return add(" "); }

    public StringAdder dot() { return add("."); }

    @Override
    public String toString() { return content.toString(); }

    @Override
    public String get() { return content.toString(); }
}
