package com.moon.core.lang;


import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class ClassUtilTest {

    interface Interface0 {
    }

    interface Interface1 extends Interface0 {
    }

    abstract static class Class0 implements Interface1 {
    }

    abstract static class Class1 extends Class0 {
    }

    @org.junit.Test
    public void testGetAllInterfaces() {
        System.out.println(ClassUtil.getAllInterfaces(Class1.class));
        List<Class> list = ClassUtil.getAllInterfaces(ArrayList.class);
        System.out.println(list);
    }

    @org.junit.Test
    public void testGetAllSuperclasses() {
    }

    @org.junit.Test
    public void testIsAssignableFrom() {
    }

    @org.junit.Test
    public void testIsInstanceOf() {
    }

    @org.junit.Test
    public void testIsInnerClass() {
    }

    @org.junit.Test
    public void testGetClasses() {
    }

    @org.junit.Test
    public void testToWrapperClass() {
    }

    @org.junit.Test
    public void testToPrimitiveClass() {
    }

    @org.junit.Test
    public void testForName() {
    }
}
