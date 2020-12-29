package com.moon.processor.utils;

/**
 * @author benshaoye
 */
public class HolderGroup {

    private final static Holder[] EMPTY = new Holder[0];

    private final Holder[] holders;

    HolderGroup(Holder[] holders) { this.holders = holders == null ? EMPTY : holders; }

    public String on(String template, String... vars) {
        for (int i = 0; i < holders.length; i++) {
            template = holders[i].on(template, vars[i]);
        }
        return template;
    }
}
