package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class MappingAdder {

    private final StringBuilder content;

    public MappingAdder() { this.content = new StringBuilder(); }

    public void add(Object obj) { add(obj == null ? null : obj.toString()); }

    public MappingAdder add(CharSequence content) {
        this.content.append(content);
        return this;
    }

    @Override
    public String toString() { return content.toString(); }
}
