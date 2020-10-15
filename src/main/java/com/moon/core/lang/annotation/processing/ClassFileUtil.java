package com.moon.core.lang.annotation.processing;

import com.moon.core.lang.annotation.builder.ClassBuilder;

/**
 * @author moonsky
 */
public class ClassFileUtil {

    public static ClassBuilder atPackage(String packageName) {
        return null;
    }

    public static ClassBuilder atPackage(Class thatClass) {
        return atPackage(thatClass.getPackage().getName());
    }

    public static ClassBuilder atPackage(Class thatClass,String subpackage) {
        return atPackage(thatClass.getPackage().getName() + '.' + subpackage);
    }
}
