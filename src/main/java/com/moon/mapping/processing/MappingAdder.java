package com.moon.mapping.processing;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
final class MappingAdder implements Supplier<String> {

    private final StringBuilder content;

    public MappingAdder() { this.content = new StringBuilder(); }

    public MappingAdder add(Object obj) { return add(obj == null ? null : obj.toString()); }

    public MappingAdder add(CharSequence content) {
        this.content.append(content);
        return this;
    }

    public MappingAdder addSpace(){
        return add(" ");
    }

    @Override
    public String toString() { return content.toString(); }

    @Override
    public String get() { return content.toString(); }
}
