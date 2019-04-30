package com.moon.core.io;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class FileTravellerTestTest {

    String path;

    @Test
    void testTraverse() {
        path = "D:\\tmp";
        FileUtil.traverseDirectory(path)
            .forEach(System.out::println);
    }
}