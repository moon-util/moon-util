package com.moon.core.lang;

import com.moon.core.lang.ref.IntAccessor;
import org.junit.jupiter.api.Test;

import static com.moon.core.lang.ParseSupportUtil.parseNum;
import static com.moon.core.lang.ParseSupportUtil.parseStr;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
public class SupportUtilTest {

    @Test
    void testParseStr() {
        char[] chars = "111\\'222 '".toCharArray();
        IntAccessor indexer = IntAccessor.of();
        String str = parseStr(chars, indexer, '\'');
        assertEquals(str, "111'222 ");

        chars = "111".toCharArray();
        indexer = IntAccessor.of(1);
        assertEquals(parseNum(chars, indexer, chars.length, chars[0]), 111);

        chars = "2_111".toCharArray();
        indexer = IntAccessor.of(1);
        assertEquals(parseNum(chars, indexer, chars.length, chars[0]), 2111);

        chars = "3_222_111".toCharArray();
        indexer = IntAccessor.of(1);
        assertEquals(parseNum(chars, indexer, chars.length, chars[0]), 3222111);

        chars = "3_222__111".toCharArray();
        indexer = IntAccessor.of(1);
        assertEquals(parseNum(chars, indexer, chars.length, chars[0]), 3222111);

        chars = "3_222__111.123_456".toCharArray();
        indexer = IntAccessor.of(1);
        assertEquals(parseNum(chars, indexer, chars.length, chars[0]), 3222111.123456D);

        chars = "3_222__111.123_456___".toCharArray();
        indexer = IntAccessor.of(1);
        assertEquals(parseNum(chars, indexer, chars.length, chars[0]), 3222111.123456D);
    }
}
