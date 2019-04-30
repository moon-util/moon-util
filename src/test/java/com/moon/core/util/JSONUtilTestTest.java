package com.moon.core.util;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class JSONUtilTestTest {

    @Test
    void testReadJsonString() {
        String filename = "d:/invoice.json";
        String json = JSONUtil.readJsonString(filename);
        System.out.println(json);
    }
}