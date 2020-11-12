package com.moon.mapping.processing;

/**
 * @author moonsky
 */
public enum Replacer {
    MAPPINGS,
    var,
    cast,
    name,
    type0,
    type1,
    fromName,
    toName,
    getterName,
    setterName,
    getterType,
    setterType,
    thisType,
    implType,
    thatType,
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
}
