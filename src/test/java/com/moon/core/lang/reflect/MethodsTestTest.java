package com.moon.core.lang.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author moonsky
 */
class MethodsTestTest {

    List<Method> methods;
    Method m;

    public static class Util {
        public void parse(String s, String s1) {

        }

        public void parse(CharSequence s, String s1) {

        }
    }

    @Test
    void testGetPublicMethods() {
    }
}