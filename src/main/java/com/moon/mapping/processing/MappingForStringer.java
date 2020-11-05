package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class MappingForStringer {

    private final StringBuilder content;

    public MappingForStringer() { this.content = new StringBuilder(); }

    public MappingForStringer add(Object obj) {
        return add(obj == null ? null : obj.toString());
    }

    public MappingForStringer add(CharSequence content) {
        this.content.append(content);
        return this;
    }

    @Override
    public String toString() { return content.toString(); }
}
