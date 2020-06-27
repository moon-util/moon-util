package com.moon.core.lang.reflect;

import com.moon.core.lang.ClassUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ProxyUtil {

    private final static ClassLoader LOADER = ProxyUtil.class.getClassLoader();

    private ProxyUtil() { noInstanceError(); }

    /**
     * 创建一个动态代理类
     *
     * @param obj     将要代理的对象
     * @param handler 代理拦截器
     * @param <T>     代理目标类型
     *
     * @return 动态代理类
     */
    public static <T> T newProxyInstance(T obj, InvocationHandler handler) {
        Class[] interfaces = ClassUtil.getAllInterfacesArr(obj.getClass());
        return (T) Proxy.newProxyInstance(LOADER, interfaces, handler);
    }
}
