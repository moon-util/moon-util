package com.moon.core.util.runner;

import com.moon.core.lang.reflect.MethodUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
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
    void testListSize() {
        String str = "{}.size()";
        Runner runner = RunnerUtil.parse(str);
        Object ret = runner.run();
        System.out.println(ret);
    }

    @Test
    void textMapSize() {
        String str = "{:}.size()";
        Runner runner = RunnerUtil.parse(str);
        Object ret = runner.run();
        System.out.println(ret);

        str = "{}.size()";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);

        str = "{11,234,2342456,'asdfasdf',,}.size()";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);

        str = "{:}.isEmpty()";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);

        str = "{}.isEmpty()";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);

        str = "{11,234,2342456,'asdfasdf',,}.isEmpty()";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);

        str = "@ListUtil.isEmpty({})";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);

        str = "@MapUtil.isEmpty({:})";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);

        str = "@ListUtil.get({123456,}, 0)";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        System.out.println(ret);
    }

    @Test
    void testRegex() {
        Runner runner = RunnerUtil.parse("@String.valueOf(12)");
        Object data = runner.run();
        System.out.println(data);
    }

    @Test
    void testKeySet() {
        Object[] data = {
            new HashMap() {{
                put("name", 1);
                put("age", 3);
                put("sex", 4);
            }}, new String[]{"A", "B"},
        };
        dataMap = new RunnerDataMap(data);
        assertEquals(dataMap.size(), 5);

        assertEquals(dataMap.get(0), "A");
        assertEquals(dataMap.get(1), "B");
        assertEquals(dataMap.get("age"), 3);
    }

    @Test
    void testVariableParamsMethod() {
        Class type = getClass();

        System.out.println(String.class.isAssignableFrom(String.class));
        MethodUtil.getPublicStaticMethods(type, "run").forEach(m -> {
            System.out.println("=============================");
            System.out.println(m);
            System.out.println(m.isVarArgs());
            Class<?>[] paramTypes = m.getParameterTypes();
            System.out.println(paramTypes);
            System.out.println("-----------------------------");
            IteratorUtil.forEach(paramTypes, paramType -> {
                System.out.println(paramType);
                System.out.println(paramType.isArray());
            });
        });
    }

    public static void run(String... values) {}

    public static void run(Object... values) {}

    public static void run(String value, Object... values) {}
}