package com.moon.core.util.runner.core;

import com.moon.core.lang.reflect.MethodUtil;
import com.moon.core.util.runner.Runner;
import com.moon.core.util.runner.RunnerUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author moonsky
 */
class InvokeArgs1TestTest {

    String str, str0, str1;
    Runner runner, runner0, runner1;
    Object data, data0, data1;

    static Runner running(String str) {
        return RunnerUtil.parse(str);
    }

    @Test
    void testParse() {
        str = "@System.currentTimeMillis()";
        runner = running(str);
        System.out.println(runner);

        str = "@String.valueOf(1234567)";
        runner = running(str);
        data = runner.run();
        System.out.println(data);
        System.out.println(runner.getClass());

        str = "@Objects.hash(123)";
        runner = running(str);
        System.out.println(runner.getClass());
        data = runner.run();
        System.out.println(data);
    }

    @Test
    void testClasses() {
        Integer val = 10;
        System.out.println(int.class.isInstance(val));
    }

    @Test
    void testStaticArgs1() {
        Integer value = 10;

        InvokeAbstract.staticMethods(getClass(), "runLong").forEach(m -> {
            try {
                MethodUtil.invoke(true, m, null, value);
            } catch (Throwable t) {
                System.out.println(m.getName());
            }
        });

        System.out.println(int.class);
        System.out.println(long.class);
        System.out.println(double.class);

        int val = 10;
        run(val);
    }

    public static void run(Integer value) {
        System.out.println("Integer");
    }

    public static void run(long value) {
        System.out.println("Integer");
    }

    public static void run(Long value) {
        System.out.println("Integer");
    }

    public static void runLong(Long value) {
        System.out.println("runLong");
    }

    public static void runLongValue(long value) {
        System.out.println("runLongValue");
    }
}