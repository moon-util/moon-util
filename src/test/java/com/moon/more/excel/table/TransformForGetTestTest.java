package com.moon.more.excel.table;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.lang.reflect.ModifierUtil;
import com.moon.core.util.UnsafeUtil;
import com.moon.more.excel.BaseFactory;
import com.moon.more.excel.TableFactory;
import com.moon.more.excel.WorkbookProxy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class TransformForGetTestTest {

    @Test
    void testFindOrDefault() throws Exception {
        TransformForGet defaults = TransformForGet.DEFAULT;
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

    static class F extends BaseFactory {

        public F(WorkbookProxy proxy, BaseFactory parent) {
            super(proxy, parent);
        }

        @Override
        protected Object get() {
            return null;
        }
    }

    @Test
    void testParamType() throws Exception {
        System.out.println(TableFactory.class.getGenericSuperclass());
        System.out.println(F.class.getGenericSuperclass());
    }
}