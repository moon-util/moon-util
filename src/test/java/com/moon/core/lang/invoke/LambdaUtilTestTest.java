package com.moon.core.lang.invoke;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
class LambdaUtilTestTest {

    public static class UserDetail {

        public int getAge() {
            return 10;
        }

        public int setAge() {
            return 10;
        }
    }

    @Test
    void testGetPropertyName() throws Exception {
        String value = LambdaUtil.getPropertyName(UserDetail::getAge);
        assertEquals("age", value);
        String name = LambdaUtil.getPropertyName(UserDetail::setAge);
        assertEquals("age", name);
    }
}
