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
    static_("static"),
    params,
    return_("return"),
    ;

    private final String search;

    Holder() {this.search = name();}

    Holder(String search) {this.search = search;}

    public String getSearch() { return search; }

    public String on(String template, String replacement) {
        return String2.replaceAll(template, getSearch(), replacement);
    }

    public static HolderGroup of(Holder... holders) { return new HolderGroup(holders); }
}
