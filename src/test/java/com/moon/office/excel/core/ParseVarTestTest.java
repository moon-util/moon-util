package com.moon.office.excel.core;

import com.moon.core.enums.ArraysEnum;
import com.moon.core.lang.ref.IntAccessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author benshaoye
 */
class ParseVarTestTest {

    String[] parseKeys(String var) {
        char[] chars = var.trim().toCharArray();
        return chars.length > 0 ? ParseVar.parseKeys(chars, IntAccessor.of(), chars.length) : ArraysEnum.STRINGS.empty();
    }

    String var;
    String[] keys;

    @Test
    void testParseKeys() {
        var = " sdfuhaskdjf   ";
        keys = parseKeys(var);
        assertEquals(keys.length, 1);
        assertEquals(keys[0], "sdfuhaskdjf");

        var = " (sdfuhaskdjf)   ";
        keys = parseKeys(var);
        assertEquals(keys.length, 1);
        assertEquals(keys[0], "sdfuhaskdjf");

        var = " (sdfuhaskdjf,name)   ";
        keys = parseKeys(var);

        assertEquals(keys.length, 2);
        assertEquals(keys[0], "sdfuhaskdjf");
        assertEquals(keys[1], "name");

        assertThrows(Throwable.class, () -> parseKeys("1 "));
        assertThrows(Throwable.class, () -> parseKeys(" (name,sex,age "));

        keys = parseKeys("");
    }
}