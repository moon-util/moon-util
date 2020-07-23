package com.moon.core.util;

import com.moon.core.enums.Testers;
import com.moon.spring.annotation.verify.VerifyIdempotent;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author moonsky
 */
class PropertiesUtilTestTest {
    static String[] paths = {
        "/test1.properties",
        "/test2.properties",
        "/test3.properties",
    };
    String path = FilterUtil.requireFind(paths, Testers.isNull.not);

    @Test
    void testRefreshAll() {

    }

    @Test
    void testLoad() {
    }

    @Test
    void testGet() {
        ThrowUtils.ignoreThrowsRun(() -> {
            Map map = PropertiesUtil.get(path);
            assertNull(map);
            assertTrue(map.size() > 0);
        }, true);
    }

    @Test
    @VerifyIdempotent
    void testGetString() {
        ThrowUtils.ignoreThrowsRun(() -> {
            String email = PropertiesUtil.getString(path, "email.host");
            String email1 = PropertiesUtil.getString(path, "email.host1");
            assertNotNull(email);
            assertNull(email1);
        }, true);
    }

    @Test
    void testGetIntValue() {
    }

    @Test
    void testGetLongValue() {
    }

    @Test
    void testGetDoubleValue() {
    }

    @Test
    void testGetBooleanValue() {
    }

    @Test
    void testGetOrDefault() {
    }
}