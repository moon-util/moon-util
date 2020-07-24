package com.moon.poi.excel.render;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author moonsky
 */
class RenderHeaderParserTestTest {

    static class Employee {

        String name;

        public String getAge() {
            return null;
        }
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testName() throws IntrospectionException {
        Class type = Employee.class;
        BeanInfo info = Introspector.getBeanInfo(type);
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            System.out.println(descriptor.getName());
            System.out.println(descriptor.getDisplayName());
            System.out.println(descriptor.getShortDescription());
        }

        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {

        }
    }
}