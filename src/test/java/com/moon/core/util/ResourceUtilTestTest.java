package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ResourceUtilTestTest {

    @Test
    void testResourceExists() {
        ThrowUtil.ignoreThrowsRun(() -> {
            Console.out.println(ResourceUtil.resourceExists("D:\\cyhr_v3.sql"));
            Console.out.println(ResourceUtil.resourceExists("D:\\cyhr_v4.sql"));
            Console.out.println(ResourceUtil.resourceExists("system-util-resource-exists.txt"));
        }, true);
    }
}