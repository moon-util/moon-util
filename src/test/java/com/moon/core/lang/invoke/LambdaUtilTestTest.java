package com.moon.core.lang.invoke;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
class LambdaUtilTestTest {

    public static class UserDetail {

        private int B;
        private int i;
        private String nType;
        private String AType;

        public int getI() {
            return i;
        }

        public int getB() {
            return B;
        }

        public String getnType() {
            return nType;
        }

        public int getAge() {
            return 10;
        }

        public int setAge() {
            return 10;
        }

        public String getAType() {
            return AType;
        }

        public boolean isA(){
            return false;
        }
    }

    @Test
    void testGetPropertyName() throws Exception {
        String value = LambdaUtil.getPropertyName(UserDetail::getAge);
        assertEquals("age", value);
        value = LambdaUtil.getPropertyName(UserDetail::getAge);
        assertEquals("age", value);
        String name = LambdaUtil.getPropertyName(UserDetail::setAge);
        assertEquals("age", name);
        assertEquals("a", LambdaUtil.getPropertyName(UserDetail::isA));
        assertEquals("nType", LambdaUtil.getPropertyName(UserDetail::getnType));
        assertEquals("AType", LambdaUtil.getPropertyName(UserDetail::getAType));
        assertEquals("i", LambdaUtil.getPropertyName(UserDetail::getI));
        assertEquals("b", LambdaUtil.getPropertyName(UserDetail::getB));
    }
}
