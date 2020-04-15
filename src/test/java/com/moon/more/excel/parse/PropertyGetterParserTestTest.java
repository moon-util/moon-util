package com.moon.more.excel.parse;

import com.moon.core.lang.reflect.FieldUtil;
import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import com.moon.more.excel.annotation.DataIndexer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class PropertyGetterParserTestTest {

    @BeforeEach
    void setUp() { }

    public static class Emp4 {}

    public static class Employee3 {

        private List<String> names;

        public List<Integer> getAge() {
            return null;
        }

        public List getSex() {
            return null;
        }

        public Map getKeywords() {
            return null;
        }

        public Map<String, Integer> getRemarks() {
            return null;
        }

        public Map<String, Map<Integer, CharSequence>> getOther() {
            return null;
        }
    }

    void testGetParameterType1() throws IntrospectionException {
        Class type = Employee3.class;
        Field field = FieldUtil.getDeclaredField(type, "names");
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equalsIgnoreCase("age")) {
                System.out.println("======== age");
                Method reader = descriptor.getReadMethod();
                Class clazz = Parser.getActual(reader.getGenericReturnType(), reader.getReturnType());
                System.out.println(clazz);
            }
            if (descriptor.getName().equalsIgnoreCase("sex")) {
                System.out.println("======== sex");
                Method reader = descriptor.getReadMethod();
                Class clazz = Parser.getActual(reader.getGenericReturnType(), reader.getReturnType());
                System.out.println(clazz);
            }
            if (descriptor.getName().equalsIgnoreCase("keywords")) {
                System.out.println("======== keywords");
                Method reader = descriptor.getReadMethod();
                Class clazz = Parser.getActual(reader.getGenericReturnType(), reader.getReturnType());
                System.out.println(clazz);
            }
            if (descriptor.getName().equalsIgnoreCase("remarks")) {
                System.out.println("======== remarks");
                Method reader = descriptor.getReadMethod();
                Class clazz = Parser.getActual(reader.getGenericReturnType(), reader.getReturnType());
                System.out.println(clazz);
            }
            if (descriptor.getName().equalsIgnoreCase("other")) {
                System.out.println("======== remarks");
                Method reader = descriptor.getReadMethod();
                Class clazz = Parser.getActual(reader.getGenericReturnType(), reader.getReturnType());
                System.out.println(clazz);
            }
        }
    }

    void testGetParameterType() throws IntrospectionException {
        Class type = Employee3.class;
        Field field = FieldUtil.getDeclaredField(type, "names");
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equalsIgnoreCase("age")) {
                System.out.println("======== age");
                Method reader = descriptor.getReadMethod();
                Type genericReturnType = reader.getGenericReturnType();
                System.out.println(genericReturnType);
                System.out.println(ParamUtil.getActual(genericReturnType));
            }
            if (descriptor.getName().equalsIgnoreCase("sex")) {
                System.out.println("======== sex");
                Method reader = descriptor.getReadMethod();
                Type genericReturnType = reader.getGenericReturnType();
                System.out.println(genericReturnType);
                System.out.println(ParamUtil.getActual(genericReturnType));
            }
            if (descriptor.getName().equalsIgnoreCase("keywords")) {
                System.out.println("======== keywords");
                Method reader = descriptor.getReadMethod();
                Type genericReturnType = reader.getGenericReturnType();
                System.out.println(genericReturnType);
                System.out.println(ParamUtil.getActual(genericReturnType));
            }
            if (descriptor.getName().equalsIgnoreCase("remarks")) {
                System.out.println("======== remarks");
                Method reader = descriptor.getReadMethod();
                Type genericReturnType = reader.getGenericReturnType();
                System.out.println(genericReturnType);
                System.out.println(ParamUtil.getActual(genericReturnType));
                System.out.println(ParamUtil.getActual(genericReturnType, 1));
            }
        }

        System.out.println("================================================");
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;
            // pType.getActualTypeArguments()
        }
        System.out.println(field.getAnnotatedType());
        System.out.println(field.getGenericType().getClass());
        System.out.println(field.getGenericType().getClass());
    }

    public static class Employee21 {

        @DataColumn
        private String name;

        @DataIndexer
        private String sex;

        @DataIndexer
        @DataColumnFlatten
        public int getAge() {
            return 20;
        }

        @DataIndexer(ending = true)
        public int getEmail() {
            return 20;
        }
    }

    public static class Employee2 {

        @DataColumn
        private String name;

        @DataIndexer
        private String sex;

        @DataIndexer
        @DataColumnFlatten
        public int getAge() {
            return 20;
        }

        @DataIndexer
        public int getEmail() {
            return 20;
        }
    }


    public static class Employee1 {

        @DataColumn
        private String name;
        private String sex;

        @DataIndexer
        @DataColumnFlatten
        public int getAge() {
            return 20;
        }

        @DataIndexer
        public int getEmail() {
            return 20;
        }
    }

    public static class Employee0 {

        @DataColumn
        private String name;
        private String sex;

        @DataColumnFlatten
        public int getAge() {
            return 20;
        }

        @DataIndexer
        public int getEmail() {
            return 20;
        }
    }

    @Test
    void testParsed1() {
        ParsedDetail<DefinedGet> parsed = ParseUtil.parseGetter(Employee0.class);

        assertTrue(parsed.hasStarting());
        assertFalse(parsed.hasEnding());
        assertEquals(2, parsed.columns.size());

        parsed = ParseUtil.parseGetter(Employee1.class);

        assertTrue(parsed.hasStarting());
        assertFalse(parsed.hasEnding());
        assertEquals(2, parsed.columns.size());

        parsed = ParseUtil.parseGetter(Employee2.class);

        assertTrue(parsed.hasStarting());
        assertFalse(parsed.hasEnding());
        assertEquals(2, parsed.columns.size());

        parsed = ParseUtil.parseGetter(Employee21.class);

        assertTrue(parsed.hasStarting());
        assertTrue(parsed.hasEnding());
        assertEquals(2, parsed.columns.size());
    }

    public static class Employee {

        @DataColumn
        private String name;
        private String sex;

        public int getAge() {
            return 20;
        }
    }

    @Test
    void testParsed() {
        ParsedDetail<DefinedGet> parsed = ParseUtil.parseGetter(Employee.class);

        assertEquals(1, parsed.columns.size());
    }

    @Test
    void testName() {
        ParseUtil.parseGetter(Employee.class);
    }

    @Test
    void testNotAllowed() {
        assertThrows(Exception.class, () -> {
            try {
                ParseUtil.parseGetter(User01.class);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                throw ex;
            }
        });
        assertThrows(Exception.class, () -> {
            try {
                ParseUtil.parseGetter(User02.class);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                throw ex;
            }
        });
        // getter parser 不会解析 setter 方法上的注解，
        // 所以这里不会报错
        ParseUtil.parseGetter(User03.class);
    }

    public static class User01 {

        @DataColumn
        @DataColumnFlatten
        private String name;
    }

    public static class User02 {

        @DataColumn
        private String age;

        @DataColumnFlatten
        public String getAge() {
            return age;
        }
    }

    public static class User03 {

        @DataColumn
        private String sex;

        @DataColumnFlatten
        public void setSex(String sex) {
            this.sex = sex;
        }
    }
}