package com.moon.core.util;

import com.moon.core.lang.SystemUtil;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class LocalStorageTestTest {

    @Test
    void testGet() throws Exception {
        System.out.println((int) '\u4e00');

        System.out.println(SystemUtil.getTempDir());

        LocalStorage.of().set("123", 12);

        Object value = LocalStorage.of().get("123");
        System.out.println(value);
    }
}