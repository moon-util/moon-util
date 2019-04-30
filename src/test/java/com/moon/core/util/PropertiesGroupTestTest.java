package com.moon.core.util;

import com.moon.core.util.require.Requires;
import com.moon.core.util.runner.Runner;
import com.moon.core.util.runner.RunnerUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author benshaoye
 */
class PropertiesGroupTestTest {

    static final Requires REQUIRES = Requires.of();

    @Test
    void testCreated() {
        String str = "{'conn.url':'localhost:8080','conn.username':'moonsky','conn.password':'123456'}";
        str = "{'conn.url':'localhost:8080','conn.username':'moonsky','conn.password':'123456', 'conn': true}";
        Runner runner = RunnerUtil.parse(str);
        Map<String, String> ret = runner.run();

        PropertiesGroup group = new PropertiesGroup(ret);

        Console.out.println(group.get("conn"));
        Console.out.println(ret);
    }
}