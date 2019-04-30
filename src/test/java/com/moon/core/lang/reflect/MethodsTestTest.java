package com.moon.core.lang.reflect;

import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author benshaoye
 */
class MethodsTestTest {

    List<Method> methods;
    Method m;
    static final Requires REQUIRES = Requires.of();

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