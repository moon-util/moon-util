package com.moon.core.util.json;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author benshaoye
 */
class JSONTestTest {

    @Test
    void testParse() {
        String filename = "d:/invoice.json";
        JSON json = JSON.parse(new File(filename));
        System.out.println();
    }
}