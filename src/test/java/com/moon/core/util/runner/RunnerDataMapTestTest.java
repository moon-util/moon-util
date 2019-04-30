package com.moon.core.util.runner;

import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
class RunnerDataMapTestTest {
    static final Requires REQUIRES = Requires.of();

    Map dataMap = new RunnerDataMap();
    Map hashMap = new HashMap();

    @Test
    void testGet() {
        dataMap.put("name", 1);
        hashMap.put("name", 1);
        REQUIRES.requireEquals(dataMap.get("name"), hashMap.get("name"));
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
        REQUIRES.requireEq(dataMap.size(), 5);

        REQUIRES.requireEquals(dataMap.get(0), "A");
        REQUIRES.requireEquals(dataMap.get(1), "B");
        REQUIRES.requireEquals(dataMap.get("age"), 3);
    }
}