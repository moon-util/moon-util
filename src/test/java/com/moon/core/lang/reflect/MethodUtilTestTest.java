package com.moon.core.lang.reflect;

import com.moon.core.util.Console;
import com.moon.core.util.IteratorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author benshaoye
 */
class MethodUtilTestTest {

    interface Top {
        void reset();

        default void use() {
        }
    }

    static class User implements Top {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public void reset() {

        }
    }

    String name;
    Class type;
    Method method, m;
    List<Method> methods;
    Object data;

    @Test
    void testGetPublicMethod() {
        type = User.class;
        name = "hashCode";
        m = MethodUtil.getPublicMethod(type, name);
        assertNotNull(m);
        assertEquals(m.getName(), name);
        assertEquals(m.getDeclaringClass(), type);

        name = "toString";
        m = MethodUtil.getPublicMethod(type, name);
        assertNotNull(m);
        assertEquals(m.getName(), name);
        assertEquals(m.getDeclaringClass(), Object.class);

        name = "apply";
        m = MethodUtil.getPublicMethod(type, name);
        assertNotNull(m);
        assertEquals(m.getName(), name);
        assertEquals(m.getDeclaringClass(), Top.class);

        name = "reset";
        m = MethodUtil.getPublicMethod(type, name);
        assertNotNull(m);
        assertEquals(m.getName(), name);
        assertEquals(m.getDeclaringClass(), type);
    }

    @Test
    void testGetDeclaredMethod() {

    }

    @Test
    void testGetPublicMethods() {
    }

    @Test
    void testGetPublicStaticMethods() {
    }

    @Test
    void testGetPublicMemberMethods() {
    }

    @Test
    void testGetDeclaredMethods() {
    }

    @Test
    void testGetDeclaredStaticMethods() {
    }

    @Test
    void testGetDeclaredMemberMethods() {
    }

    @Test
    void testGetDeclaredMethods1() {
    }

    @Test
    void testGetAllDeclaredMethods() {
    }

    @Test
    void testGetAllDeclaredStaticMethods() {
    }

    public static class Employee {
        int age = 20;
    }

    @Test
    void testGetAllDeclaredMemberMethods() {
        type = User.class;
        methods = MethodUtil.getAllMethods(type);
        IteratorUtil.forEach(methods, m -> {
            Console.out.println(m);
        });
        Console.out.println("==============================");

        data = new ArrayList() {{
            add(new Employee());
            add(new Employee());
        }};

        type = data.getClass();
        methods = MethodUtil.getAllMethods(type);
        IteratorUtil.forEach(methods, m -> {
            Console.out.println(m);
        });

        type = data.getClass();
        method = MethodUtil.getPublicMethod(type, "get", Integer.class);
        Console.out.println("===================================================");
        Console.out.println(method);
    }

    @Test
    void testInvoke() {
    }

    @Test
    void testInvokeStatic() {
    }

}