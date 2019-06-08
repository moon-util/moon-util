package com.moon.core.util.runner.core;

import com.moon.core.lang.ref.FinalAccessor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author benshaoye
 */
class InvokeTestTest {

    Class type, defaultType;
    Method method, m, m1, m0, method0, method1;
    String name, name0, name1, name2, methodName, methodName0;

    @Test
    void testGetStaticEmptyParam() {
        type = Objects.class;
        name = "hash";
        method = Invoke.getStaticEmptyParam(type, name);
        assertNotNull(method);
        assertNotNull(Invoke.getStaticEmptyParam(getClass(), "get"));

        FinalAccessor<Class> accessor = FinalAccessor.of();
        FinalAccessor<String> nameAccessor = FinalAccessor.of();

        accessor.set(getClass());
        nameAccessor.set("run");
        assertThrows(Throwable.class, () -> {
            try {
                Invoke.getStaticEmptyParam(accessor.get(), nameAccessor.get());
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
}