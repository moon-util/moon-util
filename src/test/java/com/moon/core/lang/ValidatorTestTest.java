package com.moon.core.lang;

import com.moon.core.util.UnicodeUtil;
import com.moon.core.util.validator.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author moonsky
 */
class ValidatorTestTest {

    static class Employee {

        private int age;
        private String id;
        private String name;
        private boolean sex;
        private String account;
        private String address;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Employee{");
            sb.append("age=").append(age);
            sb.append(", id='").append(id).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", sex=").append(sex);
            sb.append(", account='").append(account).append('\'');
            sb.append(", address='").append(address).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Test
    void testName() throws Exception {
        System.out.println("\u9FFF".codePointAt(0));
    }
}