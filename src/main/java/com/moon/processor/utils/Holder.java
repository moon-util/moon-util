package com.moon.processor.utils;

/**
 * @author benshaoye
 */
public enum Holder {
    /**
     * names
     */
    var,
    name,
    type,
    type0,
    type1,
    field,
    value,
    getter,
    setter,
    fromName,
    toName,
    static_("static"),
    params,
    return_("return"),
    ;

    private final String search;

    Holder() { this.search = wrap(name()); }

    Holder(String search) { this.search = wrap(search); }

    public String getSearch() { return search; }

    public String on(String template, String replacement) {
        return String2.replaceAll(template, getSearch(), replacement);
    }

    public static HolderGroup of(Holder... holders) { return new HolderGroup(holders); }

    private static String wrap(String string) { return "{" + string + "}"; }
}
