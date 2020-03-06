package com.moon.core.util;

import com.moon.core.lang.reflect.FieldUtil;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author benshaoye
 */
class MapperUtilTestTest {

    Object data;

    @Test
    void testToInstance() {

    }

    @Test
    void testToInstance1() {
    }

    @Test
    void testOverride() {

    }

    @Test
    void testForEachToMap() {
        List<Employee> employees = Employee.list(20);
        List<Map<String, Object>> maps = MapperUtil.forEachToMap(employees);

        IteratorUtil.forEach(maps, (item, i) -> item.forEach((key, value) -> {
            Employee one = employees.get(i);
            assertEquals(value, FieldUtil.getValue(key, one));
        }));
    }

    @Test
    void testForEachToInstance() {

        List<Employee> employees = Employee.list(20);
        List<Map<String, Object>> maps = MapperUtil.forEachToMap(employees);

        List<Employee> newList = MapperUtil.forEachToInstance(maps, Employee.class);
        IteratorUtil.forEach((newList), (item, i) -> assertTrue(item.equals(employees.get(i))));

        List<Employee> employeeList = Employee.list(5);
        employeeList.forEach(System.out::println);
        List<User> users = MapperUtil.forEachToOther(employeeList, User.class);
        users.forEach(System.out::println);
        List<UserVO> userVOS = MapperUtil.forEachToOther(users, UserVO.class);
        userVOS.forEach(System.out::println);
    }

    Object res = 0x4e00;

    @Test
    void testForEachToOtherInstance() {

    }

    static ThreadLocalRandom random = ThreadLocalRandom.current();

    @Data
    public static class User {

        String name;
        int age;
    }

    @Data
    @ToString(callSuper = true)
    public static class UserVO extends User {}

    @Data
    public static class Employee {

        String name;
        int age;

        public Employee() {
        }

        static Employee one() {
            Employee one = new Employee();
            one.name = "李四";
            one.age = random.nextInt(14, 65);
            return one;
        }

        static List<Employee> list(int size) {
            List<Employee> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(one());
            }
            return list;
        }

        static List<Employee> list() {
            return list(random.nextInt(10, 100));
        }

        public Employee(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    void testToMap() {
        Employee employee1 = new Employee("张三", 24);
        Map<String, Object> map = MapperUtil.toMap(employee1);
        assertTrue(map.containsKey("name"));
        assertEquals(map.get("name"), "张三");
        assertTrue(map.containsKey("age"));
        assertEquals(map.get("age"), 24);

        Employee e1 = MapperUtil.override(map, new Employee());
        assertTrue(employee1 != e1);
        assertEquals(employee1, e1);

        System.out.println(System.identityHashCode(employee1));
        System.out.println(System.identityHashCode(e1));

        Employee e2 = MapperUtil.toInstance(map, Employee.class);
        assertTrue(employee1 != e2);
        assertEquals(employee1, e1);
        assertEquals(e2, e1);

        System.out.println(System.identityHashCode(employee1));
        System.out.println(System.identityHashCode(e1));
        System.out.println(System.identityHashCode(e2));
    }
}