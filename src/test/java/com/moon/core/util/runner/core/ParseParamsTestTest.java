package com.moon.core.util.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ParseParamsTestTest {

    static final Requires REQUIRES = Requires.of();

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
        REQUIRES.requireEq(res.length, 0);
        res = parse("(,)");
        REQUIRES.requireEq(res.length, 1);
        res = parse("(null,)");
        REQUIRES.requireEq(res.length, 1);
        res = parse("(null,null)");
        REQUIRES.requireEq(res.length, 2);
        res = parse("('null',12)");
        REQUIRES.requireEq(res.length, 2);
        res = parse("(12.3,12)");
        REQUIRES.requireEq(res.length, 2);
        res = parse("(12.3,12,)");
        REQUIRES.requireEq(res.length, 2);
        res = parse("(    )");
        REQUIRES.requireEq(res.length, 0);
        res = parse("(   , )  ");
        REQUIRES.requireEq(res.length, 1);
        res = parse("  (   null ,    )");
        REQUIRES.requireEq(res.length, 1);
        res = parse("(null  ,   null)");
        REQUIRES.requireEq(res.length, 2);
        res = parse("  (  ' null'   ,12)");
        REQUIRES.requireEq(res.length, 2);
        res = parse("  (12.3  ,   12   )");
        REQUIRES.requireEq(res.length, 2);
        res = parse("( 12.3  ,  12,   )");
        REQUIRES.requireEq(res.length, 2);
        res = parse("( 12.3  ,  12,   true)");
        REQUIRES.requireEq(res.length, 3);
        res = parse("( 12.3  ,  12, name,  true)");
        REQUIRES.requireEq(res.length, 4);
        res = parse("( 12.3  ,  12, name,  ,'',   true)");
        REQUIRES.requireEq(res.length, 5);
        res = parse("( 12.3  ,  12, name, , ,'',   true)");
        REQUIRES.requireEq(res.length, 6);
        res = parse("( 12.3  ,  12, name, null , ,'',   true)");
        REQUIRES.requireEq(res.length, 6);
        res = parse("( 12.3  ,  12, name, null,  , ,'',   true)");
        REQUIRES.requireEq(res.length, 7);
    }
}