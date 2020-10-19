package com.moon.core.util;

import com.moon.runner.Runner;
import com.moon.runner.RunnerUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author moonsky
 */
class PropertiesGroupTestTest {

    @Test
    void testCreated() {
        String str = "{'conn.url':'localhost:8080','conn.username':'moonsky','conn.password':'123456'}";
        str = "{'conn.url':'localhost:8080','conn.username':'moonsky','conn.password':'123456', 'conn': true}";
        Runner<Map<String, String>> runner = RunnerUtil.parse(str);
        Map<String, String> ret = runner.run();

        PropertiesGroup group = new PropertiesGroup(ret);

        System.out.println(group.get("conn"));
        System.out.println(ret);
        System.out.println(group);
    }
}