package com.moon.core.util.json;

import com.moon.core.lang.ThrowUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author benshaoye
 */
class JSONTestTest {

    @Test
    void testParse() {
        ThrowUtil.ignoreThrowsRun(()->{
            String filename = "d:/invoice.json";
            JSON json = JSON.parse(new File(filename));
        }, true);
    }
}