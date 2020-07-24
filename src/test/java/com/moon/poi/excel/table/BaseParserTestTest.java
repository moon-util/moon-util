package com.moon.poi.excel.table;

import org.junit.jupiter.api.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class BaseParserTestTest {

    public static class Class0 {

        private String name;

        public String getName() { return name; }
    }

    public static class Class1 extends Class0 {

        private String address;

        public String getAddress() {
            return address;
        }
    }

    public static class Class2 extends Class0 {

        private String address;

        public String getAddress() {
            return address;
        }
    }

    private void formatter(PropertyDescriptor[] descriptors) throws IntrospectionException {

        for (PropertyDescriptor prop : descriptors) {

            System.out.println(prop.getName());
        }
        System.out.println(descriptors.length);
        System.out.println("===================");
    }


    @Test
    void testGetCreator() throws Exception {
        BeanInfo info0 = Introspector.getBeanInfo(Class1.class);
        BeanInfo info1 = Introspector.getBeanInfo(Class1.class, Object.class);

        formatter(info0.getPropertyDescriptors());
        formatter(info1.getPropertyDescriptors());
    }
}