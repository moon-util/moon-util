package com.moon.core.script;

import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ScriptUtilTestTest {
    static final Requires REQUIRES = Requires.of();
    Object res;

    @Test
    void testRunJSCode() {
        res = ScriptUtil.runJSCode("1+1");
        REQUIRES.requireEquals(res, 2);
    }
}