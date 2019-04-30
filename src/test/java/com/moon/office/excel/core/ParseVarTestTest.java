package com.moon.office.excel.core;

import com.moon.core.enums.ArraysEnum;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ParseVarTestTest {

    String[] parseKeys(String var) {
        char[] chars = var.trim().toCharArray();
        return chars.length > 0 ? ParseVar.parseKeys(chars, IntAccessor.of(), chars.length) : ArraysEnum.STRINGS.empty();
    }

    static final Requires REQUIRES = Requires.of();

    String var;
    String[] keys;

    @Test
    void testParseKeys() {
        var = " sdfuhaskdjf   ";
        keys = parseKeys(var);
        REQUIRES.requireEq(keys.length, 1);
        REQUIRES.requireEquals(keys[0], "sdfuhaskdjf");

        var = " (sdfuhaskdjf)   ";
        keys = parseKeys(var);
        REQUIRES.requireEq(keys.length, 1);
        REQUIRES.requireEquals(keys[0], "sdfuhaskdjf");

        var = " (sdfuhaskdjf,name)   ";
        keys = parseKeys(var);

        REQUIRES.requireEq(keys.length, 2);
        REQUIRES.requireEquals(keys[0], "sdfuhaskdjf");
        REQUIRES.requireEquals(keys[1], "name");

        REQUIRES.requireThrows(() -> parseKeys("1 "));
        REQUIRES.requireThrows(() -> parseKeys(" (name,sex,age "));

        keys = parseKeys("");
    }
}