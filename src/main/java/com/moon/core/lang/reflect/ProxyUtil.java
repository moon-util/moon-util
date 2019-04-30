package com.moon.core.lang.reflect;

import com.moon.core.lang.ClassUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ProxyUtil {

    private final static ClassLoader LOADER = ProxyUtil.class.getClassLoader();

    private ProxyUtil() {
        noInstanceError();
    }

    public static <T> T newProxyInstance(T obj, InvocationHandler handler){
        Class type = obj.getClass();
        List<Class> classes = ClassUtil.getAllInterfaces(type);
        Class[] interfaces = classes.toArray(new Class[classes.size()]);
        return (T) Proxy.newProxyInstance(LOADER, interfaces, handler);
    }
}
