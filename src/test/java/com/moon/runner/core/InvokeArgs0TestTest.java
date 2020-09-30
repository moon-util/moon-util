package com.moon.runner.core;

import com.moon.core.AbstractTest;
import com.moon.core.lang.ref.FinalAccessor;
import com.moon.runner.Runner;
import com.moon.runner.RunnerUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class InvokeArgs0TestTest extends AbstractTest {

    Class type, defaultType;
    Method method, m, m1, m0, method0, method1;
    String name, name0, name1, name2, methodName, methodName0;

    @Test
    void testArraysCopyOfRange() {
        int[] arr = {1, 1, 1, 1, 2, 2, 2, 2};
        System.out.println(Arrays.toString(Arrays.copyOfRange(arr, 0, 3)));
        System.out.println(Arrays.toString(Arrays.copyOfRange(arr, 3, arr.length)));
    }

    @Test
    void testOnlyMethod() {
        String str = "@Objects.hash(12, 12)";
        Runner runner = RunnerUtil.parse(str);

        Object ret = runner.run();
        Object res = Objects.hash(12, 12);
        assertEquals(ret, res);

        str = "@Objects.hash(12, 12, 1, 1, 1, 1, 2, 2, 2, 2)";
        runner = RunnerUtil.parse(str);
        ret = runner.run();
        res = Objects.hash(12, 12, 1, 1, 1, 1, 2, 2, 2, 2);
        assertEquals(ret, res);
    }

    @Test
    void testOnlyMethod0() {
        String str;
        Object data = null;
        Runner runner;

        str = "'123' . endsWith (  '23'   )";
        runner = RunnerUtil.parse(str);

        assertEquals(runner.run(), "123".endsWith("23"));

        int count = Integer.MAX_VALUE;

        runningTime(count, () -> runner.run());
        runningTime(count, () -> "123".endsWith("23"));
    }

    @Test
    void testGetStaticEmptyParam() {
        type = Objects.class;
        name = "hash";
        method = InvokeArgs0.staticArgs0(type, name);
        assertNotNull(method);
        assertNotNull(InvokeArgs0.staticArgs0(getClass(), "get"));

        FinalAccessor<Class> accessor = FinalAccessor.of();
        FinalAccessor<String> nameAccessor = FinalAccessor.of();

        accessor.set(getClass());
        nameAccessor.set("run");
        assertThrows(Throwable.class, () -> {
            try {
                InvokeArgs0.staticArgs0(accessor.get(), nameAccessor.get());
            } catch (Throwable t) {
                System.out.println(t.getMessage());
                throw t;
            }
        });
    }

    public static void get(Object... vs) {}

    public static void get(String... vs) {}

    public static void run(Object... vs) {}

    public static void run(String... vs) {}

    public static void run(Number... vs) {}

    final static String STRING = "1234567890";

    static class C0 {

        @Override
        public String toString() { return "C0"; }
    }

    static class C1 extends C0 {

        @Override
        public String toString() { return "C1"; }
    }

    static class C2 extends C1 {

        @Override
        public String toString() { return "C2"; }
    }

    static class C3 extends C2 {

        @Override
        public String toString() { return "C3"; }
    }

    @Test
    void testToString() throws InvocationTargetException, IllegalAccessException {
        method = InvokeArgs0.memberArgs0(Object.class, "toString");

        C0 o0 = new C0();
        C1 o1 = new C1();
        C2 o2 = new C2();
        C3 o3 = new C3();

        assertEquals(method.invoke(o0), o0.getClass().getSimpleName());
        assertEquals(method.invoke(o1), o1.getClass().getSimpleName());
        assertEquals(method.invoke(o2), o2.getClass().getSimpleName());
        assertEquals(method.invoke(o3), o3.getClass().getSimpleName());
    }
}
