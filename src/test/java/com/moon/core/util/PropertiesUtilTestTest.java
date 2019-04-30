package com.moon.core.util;

import com.moon.core.enums.Predicates;
import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.moon.core.util.require.Requires.of;

/**
 * @author benshaoye
 */
class PropertiesUtilTestTest {
    static final Requires REQUIRES = of();
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
        REQUIRES.requireNotNull(map);
        REQUIRES.requireNotEmpty(map);
    }

    @Test
    void testGetString() {
//        final Requires REQUIRES = Requires.ofPrintln();
        String email = PropertiesUtil.getString(path, "email.host");
        String email1 = PropertiesUtil.getString(path, "email.host1");

        REQUIRES.requireNotNull(email);
        REQUIRES.requireNull(email1);
        REQUIRES.requireNull(email1);
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