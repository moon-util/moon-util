package com.moon.spring.jpa.identity;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.Assert;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import org.hibernate.id.IdentifierGenerator;

import java.lang.reflect.Constructor;

/**
 * @author benshaoye
 */
final class IdentifierUtil {

    private IdentifierUtil() { }

    public static IdentifierGenerator newInstance(String description) {
        Assert.hasText(description);
        String[] descriptions = description.split(":");
        Class type = ClassUtil.forName(descriptions[0]);
        final int length = descriptions.length;
        if (length == 1) {
            return (IdentifierGenerator) ConstructorUtil.newInstance(type);
        }
        final int argsLen = length - 1;
        TypeCaster caster = TypeUtil.cast();
        for (Constructor constructor : type.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == argsLen) {
                try {
                    Object[] args = new Object[argsLen];
                    Class[] types = constructor.getParameterTypes();
                    for (int i = 0; i < argsLen; i++) {
                        Class argType = types[i];
                        String param = descriptions[i + 1];
                        args[i] = caster.toType(param, argType);
                    }
                    return (IdentifierGenerator) ConstructorUtil.newInstance(type, args);
                } catch (Throwable t) {
                    continue;
                }
            }
        }
        throw new IllegalStateException(description);
    }
}
