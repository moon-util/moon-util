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
    var0,
    var1,
    cast,
    name,
    NULL,
    type0,
    type1,
    type2,
    type3,
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
    simpleName,
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
