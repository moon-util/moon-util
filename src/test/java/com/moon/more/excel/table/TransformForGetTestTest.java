package com.moon.more.excel.table;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.lang.reflect.ModifierUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class TransformForGetTestTest {

    @Test
    void testFindOrDefault() throws Exception {
        TransferForGet defaults = TransferForGet.DEFAULT;
        defaults.test(null);
    }

    class Inner {}

    static class Inner0 {}

    @Test
    void testInner() throws Exception {
        assertNull(getClass().getDeclaringClass());
        System.out.println(ArrayUtil.stringify(getClass().getDeclaredClasses()));
        assertTrue(Inner.class.isMemberClass());
        assertTrue(Inner0.class.isMemberClass());
        assertFalse(getClass().isMemberClass());
        assertTrue(ModifierUtil.isStatic(Inner0.class));
        assertFalse(ModifierUtil.isStatic(Inner.class));
    }
}