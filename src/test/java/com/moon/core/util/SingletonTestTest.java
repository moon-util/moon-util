package com.moon.core.util;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.reflect.MethodUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class SingletonTestTest {

    @Test
    void testGetInstance() throws Exception {

        Singleton singleton = Singleton.getInstance();

        Class singletonClass = ClassUtil.forName("com.moon.core.util.Singleton$SingletonImpl");

        Constructor constructor = singletonClass.getDeclaredConstructor();
        constructor.setAccessible(true);

        Object created = constructor.newInstance();

        assertTrue(singleton != created);
        assertTrue(singleton.getClass() == created.getClass());

        List<Method> ms = MethodUtil.getDeclaredMethods(singletonClass, "add");
        assertTrue(ms.size() == 1);

        Method method = ms.get(0);
        try {
            method.invoke(created);
        } catch (Exception e) {
            // ignore
        }

        method.setAccessible(true);
        method.invoke(singleton);
    }
}