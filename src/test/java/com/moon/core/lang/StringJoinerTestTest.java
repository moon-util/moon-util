package com.moon.core.lang;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class StringJoinerTestTest {

    @Test
    void testOf() {
        assertDoesNotThrow(() -> {
            StringJoiner.of("");
        });
    }

    @Test
    void testAdd() {
        Object obj = null;
        StringJoiner joiner = JoinerUtil.of(",", "(", ")").add("aaa").add(obj).add("bbb").add(null);
        assertEquals(joiner.toString(), "(aaa,null,bbb,null)");

        int[] array = null;
        JoinerUtil.of(",").join(array);
    }

    @Test
    void testJoin() {
        Object obj = null;
        List<String> list = new ArrayList() {{
            add("aaaa");
            add("bbbb");
            add(null);
            add("cccc");
            add("dddd");
            add("eeee");
        }};
        StringJoiner joiner = JoinerUtil.of(",", "( ", " )")
            .useForNull("本少爷")
            .add("aaa").add(obj).add("bbb").add(null)
            .appendDelimiter().setDelimiter("|")
            .useForNull("moonsky").join(list);
        System.out.println(joiner);
        joiner = JoinerUtil.of("[]").skipNulls()
            .useForNull("本少爷")
            .add("aaa").add(obj).add("bbb").add(null)
            .appendDelimiter().setDelimiter("|")
            .useForNull("moonsky").join(list);
        System.out.println(joiner);

        joiner = JoinerUtil.of(null)
            .add("SELECT * FROM table WHERE name IN (")
            .appendDelimiter().setDelimiter(",").join(list).setDelimiter(null).add(")");
        System.out.println(joiner);
    }

    @Test
    void testMerge() {
        Object obj = null;
        List<String> list = new ArrayList() {{
            add("aaaa");
            add("bbbb");
            add(null);
            add("cccc");
            add("dddd");
            add("eeee");
        }};
        JoinerUtil.of(";").join(list);
        assertEquals("aaaa;bbbb;null;cccc;dddd;eeee", JoinerUtil.of(";").join(list).toString());

        assertEquals("aaaa;bbbb;cccc;dddd;eeee", JoinerUtil.of(";").skipNulls().join(list).toString());
    }

    @Test
    void testUseForNull() {
    }

    @Test
    void testSetDelimiter() {
    }

    @Test
    void testToString() {
    }


    @Test
    void testReset() {
    }

    @Test
    void testSetStringifier() {
    }

    @Test
    void testSetPrefix() {
    }

    @Test
    void testSetSuffix() {
    }

    @Test
    void testSkipNulls() {
    }

    @Test
    void testRequireNonNull() {
    }

    @Test
    void testAddDelimiter() {
    }

    @Test
    void testAppendTo() {
    }

    @Test
    void testLength() {
        assertEquals(JoinerUtil.of(",", "123", "567").length(), 6);
        assertEquals(JoinerUtil.of(",", "123", "567").add("aaaa").length(), 10);
        assertEquals(JoinerUtil.of(",", "123", "567").add("aaaa").add("ccc").length(), 14);
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8};
        assertEquals(JoinerUtil.of(",", "123", "567").add("aaaa").join(ints).add("ccc").length(), 30);
    }

    @Test
    void testCharAt() {
        String prefix = "123", suffix = "789";

        StringJoiner joiner = JoinerUtil.of(",", prefix, suffix);
        assertion(joiner, prefix + suffix);

        joiner = JoinerUtil.of(",", "123", "567").add("abcd");
        assertion(joiner, "123abcd567");

        joiner = JoinerUtil.of(",", "123", "567").add("abcd").add("efgh");
        assertion(joiner, "123abcd,efgh567");

        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8};
        joiner = JoinerUtil.of(",", "123", "567").add("aaaa").join(ints).add("ccc");
        assertion(joiner, "123aaaa,1,2,3,4,5,6,7,8,ccc567");
    }

    void assertion(StringJoiner joiner, String str) {
        int len = str.length(), index = 0;
        assertEquals(joiner.length(), len);
        assertEquals(joiner.get(), str);
        for (; index < len; index++) {
            try {
                assertEquals(joiner.charAt(index), str.charAt(index));
            } catch (Throwable t) {
                t.printStackTrace();
                joiner.charAt(index);
            }
        }
        final int currIndex = index;
        assertEquals(currIndex, len);
        assertThrows(StringIndexOutOfBoundsException.class, () -> joiner.charAt(currIndex));
        assertThrows(StringIndexOutOfBoundsException.class, () -> joiner.charAt(currIndex), () -> String.valueOf(currIndex));
        assertThrows(StringIndexOutOfBoundsException.class, () -> joiner.charAt(len));
        assertThrows(StringIndexOutOfBoundsException.class, () -> joiner.charAt(len), () -> String.valueOf(len));
    }

    @Ignore
    @Test
    void testSubSequence() {
    }

    @Test
    void testGet() {
        int count = 10000000;
        StringBuilder sb = new StringBuilder();
        StringJoiner joiner = JoinerUtil.of(null);
        StringJoiner adder = StringJoiner.of(",");

        for (int i = 0; i < 500; i++) {
            sb.append("500");
            joiner.append("500");
            adder.append("500");
        }
        sb = new StringBuilder();
        joiner = JoinerUtil.of(null);
        adder = StringJoiner.of(",");
        long v1 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            sb.append("500");
        }
        long v2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            joiner.append("500");
        }
        long v3 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            adder.append("500");
        }
        long v4 = System.currentTimeMillis();
        System.out.println(v2 - v1);
        System.out.println(v3 - v2);
        System.out.println(v4 - v3);
    }

    @Test
    void testGetValue() {
    }

    @Test
    void testAppend() {
    }
}