package com.moon.core.lang;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

/**
 * @author moonsky
 */
public class ClassUtilTest {

    @Test
    void testGetShortName() throws Exception {
        System.out.println(ClassUtil.getShortName(getClass()));
    }

    interface Interface0 {}

    interface Interface1 extends Interface0 {}

    abstract static class Class0 implements Interface1 {}

    abstract static class Class1 extends Class0 {}

    @Test
    public void testGetAllInterfaces() {
        Set<Class> interfaces = ClassUtil.getAllInterfaces(Class1.class);
        Assertions.assertTrue(interfaces.contains(Interface0.class));
        Assertions.assertTrue(interfaces.contains(Interface1.class));
        interfaces = ClassUtil.getAllInterfaces(Class0.class);
        Assertions.assertTrue(interfaces.contains(Interface0.class));
        Assertions.assertTrue(interfaces.contains(Interface1.class));

        System.out.println(ClassUtil.getAllSuperclasses(Class1.class));

        interfaces = ClassUtil.getAllInterfaces(List.class);
        System.out.println(interfaces);
    }

    @Test
    public void testGetAllSuperclasses() {
        ClassUtil.WRAPPER_TO_PRIMITIVE_MAP.forEach((key, value) -> {
            // System.out.println(key);
            // System.out.println(value);
            Assertions.assertFalse(key.isAssignableFrom(value));
            Assertions.assertFalse(value.isAssignableFrom(key));
        });

        // System.out.println(ClassUtil.getAllSuperclasses(List.class));
    }

    @Test
    public void testIsAssignableFrom() {
    }

    @Test
    public void testIsInstanceOf() {
    }

    @Test
    public void testIsInnerClass() {
    }

    @Test
    public void testGetClasses() {
    }

    @Test
    public void testToWrapperClass() {
    }

    @Test
    public void testToPrimitiveClass() {
    }

    @Test
    public void testForName() {
    }
}
