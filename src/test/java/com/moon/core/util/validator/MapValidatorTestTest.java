package com.moon.core.util.validator;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
class MapValidatorTestTest {

    @Test
    void testOf() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "1");
        map.put("2", "1");
        map.put("3", "1");
        map.put("4", "1");
        map.put("5", "1");

    }
}