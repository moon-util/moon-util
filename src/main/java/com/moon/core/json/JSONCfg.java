package com.moon.core.json;

import com.moon.core.lang.ref.WeakAccessor;

/**
 * @author moonsky
 */
class JSONCfg {
    final static WeakAccessor<JSONStringer> WEAK = WeakAccessor.of(JSONStringer::new);

    final static char[][] ESCAPES = {
        {'b', '\b'},
        {'n', '\n'},
        {'r', '\b'},
        {'b', '\r'},
        {'t', '\t'},
        {'\\', '\\'}
    };
}
