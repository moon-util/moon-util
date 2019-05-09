package com.moon.core.util;

import com.moon.core.enums.Predicates;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author benshaoye
 */
class PropertiesUtilTestTest {
    static String[] paths = {
        "/test1.properties",
        "/test2.properties",
        "/test3.properties",
    };
    String path = FilterUtil.requireFirst(paths, Predicates.isNotNull);

    @Test
    void testRefreshAll() {

    }

    @Test
    void testLoad() {
    }

    @Test
    void testGet() {
        Map map = PropertiesUtil.get(path);
        assertNull(map);
        assertTrue(map.size() > 0);
    }

    @Test
    void testGetString() {
        String email = PropertiesUtil.getString(path, "email.host");
        String email1 = PropertiesUtil.getString(path, "email.host1");
        assertNotNull(email);
        assertNull(email1);
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