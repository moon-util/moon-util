package com.moon.core.util.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class ParseParamsTestTest {

    AsRunner[] res;

    AsRunner[] parse(String exp) {
        char[] chars = exp.toCharArray();
        IntAccessor indexer = IntAccessor.of();
        int len = chars.length;
        int curr = ParseUtil.nextVal(chars, indexer, len);
        return res = ParseParams.parse(chars, indexer, len, null);
    }

    @Test
    void testParse() {
        res = parse("()");
        assertEquals(res.length, 0);
        res = parse("(,)");
        assertEquals(res.length, 1);
        res = parse("(null,)");
        assertEquals(res.length, 1);
        res = parse("(null,null)");
        assertEquals(res.length, 2);
        res = parse("('null',12)");
        assertEquals(res.length, 2);
        res = parse("(12.3,12)");
        assertEquals(res.length, 2);
        res = parse("(12.3,12,)");
        assertEquals(res.length, 2);
        res = parse("(    )");
        assertEquals(res.length, 0);
        res = parse("(   , )  ");
        assertEquals(res.length, 1);
        res = parse("  (   null ,    )");
        assertEquals(res.length, 1);
        res = parse("(null  ,   null)");
        assertEquals(res.length, 2);
        res = parse("  (  ' null'   ,12)");
        assertEquals(res.length, 2);
        res = parse("  (12.3  ,   12   )");
        assertEquals(res.length, 2);
        res = parse("( 12.3  ,  12,   )");
        assertEquals(res.length, 2);
        res = parse("( 12.3  ,  12,   true)");
        assertEquals(res.length, 3);
        res = parse("( 12.3  ,  12, name,  true)");
        assertEquals(res.length, 4);
        res = parse("( 12.3  ,  12, name,  ,'',   true)");
        assertEquals(res.length, 5);
        res = parse("( 12.3  ,  12, name, , ,'',   true)");
        assertEquals(res.length, 6);
        res = parse("( 12.3  ,  12, name, null , ,'',   true)");
        assertEquals(res.length, 6);
        res = parse("( 12.3  ,  12, name, null,  , ,'',   true)");
        assertEquals(res.length, 7);
    }
}