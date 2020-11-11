package com.moon.mapping.processing;

import java.util.Collection;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * @author moonsky
 */
final class StringAdder implements ThrowingSupplier<String> {

    private final StringBuilder content;

    public StringAdder() { this.content = new StringBuilder(); }

    public StringAdder imports(Collection<String> classes) {
        return add(classes.stream().map(cls -> "import " + cls + ";").collect(joining("")));
    }

    public StringAdder pkg(Class<?> type) { return add(type.getPackage().getName()); }

    public StringAdder cls(Class<?> type) { return add(type.getCanonicalName()); }

    public StringAdder simpleCls(Class<?> type) { return add(type.getSimpleName());}

    public StringAdder add(Object obj) { return add(obj == null ? null : obj.toString()); }

    public StringAdder add(CharSequence content) {
        this.content.append(content);
        return this;
    }

    public StringAdder add(boolean doAdd, CharSequence content) {
        return doAdd ? add(content) : this;
    }

    public StringAdder impl(Class<?>... interfaces) {
        return impl(stream(interfaces).map(Class::getCanonicalName).toArray(String[]::new));
    }

    public StringAdder impl(String... interfaces) {
        return add(" implements ").add(String.join(",", interfaces));
    }

    public StringAdder space() { return add(" "); }

    public StringAdder dot() { return add("."); }

    @Override
    public String toString() { return content.toString(); }

    @Override
    public String get() { return content.toString(); }
}
