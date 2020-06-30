package com.moon.core.lang;

import com.moon.core.util.IteratorUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @author moonsky
 */
public class PackageUtilTest {
    @Test
    void testScan() {
        String packageName = "com.moon.core.util.env";
        // packageName = "com.moon";
        String[] packages = PackageUtil.scan(packageName);
        IteratorUtil.forEach(packages, System.out::println);
    }
}
