package com.moon.core.util;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ResourceUtilTestTest {

    @Test
    void testResourceExists() {

        Console.out.println(ResourceUtil.resourceExists("D:\\cyhr_v3.sql"));
        Console.out.println(ResourceUtil.resourceExists("D:\\cyhr_v4.sql"));
        Console.out.println(ResourceUtil.resourceExists("system-util-resource-exists.txt"));
    }
}