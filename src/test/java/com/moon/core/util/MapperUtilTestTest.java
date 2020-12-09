package com.moon.core.util;

import com.moon.core.lang.reflect.FieldUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
 * @author moonsky
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
    }

    @Test
    void testForEachToInstance() {
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
    @EqualsAndHashCode(callSuper = true)
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
    }
}