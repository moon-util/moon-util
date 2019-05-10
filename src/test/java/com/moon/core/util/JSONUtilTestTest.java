package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class JSONUtilTestTest {

    @Test
    void testReadJsonString() {
        ThrowUtil.ignoreThrowsRun(() -> {
            String filename = "d:/invoice.json";
            String json = JSONUtil.readJsonString(filename);
            System.out.println(json);
        }, true);
    }
}