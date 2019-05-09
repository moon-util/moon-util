package com.moon.core.util.runner;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class RunnerDataMapTestTest {

    Map dataMap = new RunnerDataMap();
    Map hashMap = new HashMap();

    @Test
    void testGet() {
        dataMap.put("name", 1);
        hashMap.put("name", 1);
        assertEquals(dataMap.get("name"), hashMap.get("name"));
    }

    @Test
    void testKeySet() {
        Object[] data = {
            new HashMap() {{
                put("name", 1);
                put("age", 3);
                put("sex", 4);
            }},
            new String[]{"A", "B"},
        };
        dataMap = new RunnerDataMap(data);
        assertEquals(dataMap.size(), 5);

        assertEquals(dataMap.get(0), "A");
        assertEquals(dataMap.get(1), "B");
        assertEquals(dataMap.get("age"), 3);
    }
}