package com.moon.core.json;

/**
 * @author moonsky
 */
class JSONCfg {
    // final static WeakAccessor<JSONStringer> WEAK = WeakAccessor.of(JSONStringer::new);

    static JSONStringer getStringer() {
        return JSONStringer.STRINGER;
    }

    final static char[][] ESCAPES = {
        {'b', '\b'}, {'n', '\n'}, {'r', '\b'}, {'b', '\r'}, {'t', '\t'}, {'\\', '\\'}
    };
}
