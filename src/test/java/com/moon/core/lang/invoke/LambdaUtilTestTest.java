package com.moon.core.lang.invoke;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class LambdaUtilTestTest {

    public static class UserDetail {

        public int getAge(){
            return 10;
        }
    }

    @Test
    void testGetPropertyName() throws Exception {
        String value = LambdaUtil.getPropertyName(UserDetail::getAge);
        System.out.println(value);
    }
}