package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
class GroupUtilTestTest {

    public static class Employee {

        public String name;
        public int age;

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }

        public void setAge(int age) { this.age = age; }
    }

    @Test
    void testSimplifyGroup() {
        List<Employee> list = new ArrayList<>();
        Map<String, List<Employee>> grouped = GroupUtil.groupBy(list, employee -> employee.name, ArrayList::new);
        Map<String, Employee> employeeMap = GroupUtil.simplifyGroup(list, employee -> employee.name);
        FilterUtil.findAsUtilOptional(list, employee -> employee.name == null);
    }
}