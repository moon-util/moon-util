package com.moon.processor;

import com.moon.processor.utils.String2;

/**
 * @author benshaoye
 */
public enum Holder {
    var,
    name,
    type,
    STATIC("static"),
    params,
    RETURN("return"),
    ;

    private final String search;

    Holder() {this.search = name();}

    Holder(String search) {this.search = search;}

    public String getSearch() { return search; }

    public String on(String template, String replacement) {
        return String2.replaceAll(template, getSearch(), replacement);
    }

    public static Group of(Holder... holders) {
        return new Group(holders);
    }

    public static class Group {

        private final Holder[] holders;

        public Group(Holder[] holders) {
            this.holders = holders == null ? new Holder[0] : holders;
        }

        public String on(String template, String... vars) {
            for (int i = 0; i < holders.length; i++) {
                template = holders[i].on(template, vars[i]);
            }
            return template;
        }
    }
}
