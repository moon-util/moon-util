package com.moon.core.json;

import com.moon.core.lang.IntUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class JSONStringerTestTest {

    @Test
    void testStringify() throws Exception {
        int[] ints = IntUtil.toInts(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        String jsonStr = JSONCfg.getStringer().stringify(ints);
        System.out.println(jsonStr);
    }
}