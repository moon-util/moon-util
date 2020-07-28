package com.moon.core.lang;

import com.moon.core.lang.support.NumberSupport;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class NumberUtil {

    private NumberUtil() { noInstanceError(); }

    /**
     * 是否是广义数字类
     *
     * @param type 待测类
     *
     * @return true | false
     */
    public static boolean isGeneralNumberClass(Class type) {
        return isNumberWrapperClass(type) || isNumberPrimitiveClass(type) || Number.class.isAssignableFrom(type);
    }

    /**
     * 是否是数字基本数据类型类
     *
     * @param type 待测类
     *
     * @return 当 type 是 byte、short、int、long、float、double 其中之一时返回 true，否则返回 false
     */
    public static boolean isNumberPrimitiveClass(Class type) {
        return NumberSupport.isNumberPrimitiveClass(type);
    }

    /**
     * 是否是数字基本包装类型类
     *
     * @param type 待测类
     *
     * @return 当 type 是 Byte、Short、Integer、Long、Float、Double 其中之一时返回 true，否则返回 false
     */
    public static boolean isNumberWrapperClass(Class type) {
        return NumberSupport.isNumberWrapperClass(type);
    }
}
