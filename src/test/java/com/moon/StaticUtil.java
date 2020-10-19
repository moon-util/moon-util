package com.moon;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;

/**
 * @author moonsky
 */
public class StaticUtil {

    public StaticUtil() {
    }

    @Test
    void testName() throws Exception {
        ClassWriter writer = new ClassWriter(0);
        writer.newClass("com/moon/runner/StaticMethod");

        System.out.println(writer.toString());
    }
}
