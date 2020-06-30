package com.moon.core.util;

import com.moon.core.lang.reflect.MethodUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author moonsky
 */
public class OSUtilTest {
    @Test
    void testIsLinux() {
        try {
            List<Method> methodList = MethodUtil.getPublicStaticMethods(OSUtil.class);
            String[] tests = new String[methodList.size()];
            IteratorUtil.forEach(methodList, (method, index) -> {
                if (method.getParameterCount() == 0){
                    tests[index] = MethodUtil.invokeStatic(method) + "\t" + method.getName();
                } else {
                    tests[index] = method.getName();
                }
            });
            Arrays.sort(tests);
            IteratorUtil.forEach(tests, System.out::println);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
