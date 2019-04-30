package com.moon.core.lang.support;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.ListUtil;

import java.util.List;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class ClassSupport {

    private ClassSupport() {
        ThrowUtil.noInstanceError();
    }

    public static List<Class> addAllInterfaces(List<Class> list, Class[] types) {
        for (int i = 0; i < types.length; i++) {
            addAllInterfaces(list, types[i]);
        }
        return list;
    }

    public static List<Class> addAllInterfaces(List<Class> list, Class type) {
        if (type.isInterface()) {
            ListUtil.add(list, type);
        }
        addAllInterfaces(list, type.getInterfaces());
        if ((type = type.getSuperclass()) != null) {
            addAllInterfaces(list, type);
        }
        return list;
    }
}
