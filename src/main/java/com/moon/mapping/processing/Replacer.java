package com.moon.mapping.processing;

/**
 * @author moonsky
 */
public enum Replacer {
    /**
     * 替换
     */
    MAPPINGS,
    var,
    cast,
    name,
    NULL,
    type0,
    type1,
    value,
    fromName,
    toName,
    format,
    getterName,
    setterName,
    getterType,
    setterType,
    thisType,
    implType,
    thatType,
    modifiers,
    thisName {
        @Override
        String toReplacement(String value) { return ElementUtils.getSimpleName(value); }
    },
    thatName {
        @Override
        String toReplacement(String value) { return ElementUtils.getSimpleName(value); }
    },
    ;

    final String pattern;

    Replacer() { this.pattern = "\\{" + name() + "\\}"; }

    String toReplacement(String value) { return value; }

    public String replace(String template, String type) {
        return template.replaceAll(pattern, String.valueOf(toReplacement(type)));
    }

    public String replace(String template, Class<?> type) {
        return replace(template, type.getCanonicalName());
    }
}
