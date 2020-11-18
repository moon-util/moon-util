package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class CallerInfo {

    /**
     * 形如: UnsafeConvert.toCalendar
     */
    private final String caller;
    /**
     * 形如: ({var})、({var}, {var0})、({var}, {var0}, {var1}) 等
     */
    private final String params;

    private final String types;

    public CallerInfo(String caller, String params, String types) {
        this.caller = caller;
        this.params = params;
        this.types = types;
    }

    public String getCaller() { return caller; }

    public String getParams() { return params; }

    public String formatParams(String first, String... params) {
        final String var = "var";
        String t0 = getParams();
        if (first != null) {
            t0 = Replacer.var.replace(t0, first);
        }
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                String indexedP = params[i];
                if (indexedP != null) {
                    t0 = t0.replaceAll("\\{" + (var + i) + "\\}", indexedP);
                }
            }
        }
        return t0;
    }

    public String toString(String first, String... params) {
        return getCaller() + formatParams(first, params);
    }

    @Override
    public String toString() { return getCaller() + getParams(); }
}
