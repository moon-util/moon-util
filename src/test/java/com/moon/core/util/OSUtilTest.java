package com.moon.core.util;

import com.moon.core.lang.reflect.MethodUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author benshaoye
 */
public class OSUtilTest {
    @Test
    void testIsLinux() {
        try {
            List<Method> methodList = MethodUtil.getPublicStaticMethods(OSUtil.class);
            String[] tests = new String[methodList.size()];
            IteratorUtil.forEach(methodList, (method, index) ->
                tests[index] = MethodUtil.invokeStatic(method) + "\t" + method.getName());
            Arrays.sort(tests);
            IteratorUtil.forEach(tests, System.out::println);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
