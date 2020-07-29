package com.moon.core.lang;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author moonsky
 */
class SystemUtilTestTest {

    @Test
    void testResourceExists() throws IOException {
        String path = "system-util-resource-exists.txt";

        System.out.println(SystemUtil.getWorkingDir());
        System.out.println(SystemUtil.getJavaVersion());
        System.out.println(SystemUtil.getJavaVersionAsInt());
        System.out.println(SystemUtil.getJvmInfo());
        System.out.println(SystemUtil.getJvmName());
        System.out.println(SystemUtil.getJvmVersion());
        System.out.println(SystemUtil.get("user.name"));
        System.out.println("====================================================");
        SystemUtil.getAll().forEach((key, value) -> {
            System.out.println(StringUtil.format("{}: {}", key, value));
        });
    }
}