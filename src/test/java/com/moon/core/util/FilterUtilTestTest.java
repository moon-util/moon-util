package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
class FilterUtilTestTest {

    @Test
    void testEachMatched() {
        List<String> list = new ArrayList() {{
            add("a");
            add("ab");
            add("abc");
            add("abcd");
            add("abcde");
            add("abcdef");
            add("abcdefg");
            add("abcdefgh");
            add("abcdefghi");
            add("abcdefghij");
            add("abcdefghijk");
            add("abcdefghijkl");
            add("abcdefghijklm");
            add("abcdefghijklmn");
            add("abcdefghijklmno");
            add("abcdefghijklmnop");
            add("abcdefghijklmnopq");
            add("abcdefghijklmnopqr");
            add("abcdefghijklmnopqrs");
            add("abcdefghijklmnopqrst");
            add("abcdefghijklmnopqrstu");
            add("abcdefghijklmnopqrstuv");
            add("abcdefghijklmnopqrstuvw");
            add("abcdefghijklmnopqrstuvwx");
            add("abcdefghijklmnopqrstuvwxy");
            add("abcdefghijklmnopqrstuvwxyz");
        }};

        FilterUtil.forEachMatched(list, str -> str.length() > 5, str -> {
            System.out.println(str);
        });
    }
}