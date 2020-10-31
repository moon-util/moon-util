package com.moon.core.lang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class ObjectUtilTestTest {

    public static class Employee {

        public String getName() {
            return "Employee";
        }

        @Override
        public String toString() {
            return ObjectUtil.toStringAsJson(this, Employee::getName);
        }
    }

    public static class UserDetail {

        private int age;
        private String address;

        public int getAge() {
            return 10;
        }

        public String getAddress() {
            return "北京是朝阳区";
        }

        public Employee getEmployee() {
            return new Employee();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("UserDetail{");
            sb.append("age=").append(age);
            sb.append(", address='").append(address).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Test
    void testTestToString() throws Exception {
        UserDetail user = new UserDetail();
        String str = ObjectUtil.toStringAsJson(user, UserDetail::getAddress, UserDetail::getAge, UserDetail::getEmployee);
        System.out.println(str);
        System.out.println(JSON.toJSONString(user, SerializerFeature.WriteClassName));
    }
}