package com.moon.core.util.runner.core;

import com.moon.core.enums.ArraysEnum;
import com.moon.core.lang.reflect.MethodUtil;
import com.moon.core.util.FilterUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.moon.core.lang.reflect.MethodUtil.getPublicStaticMethods;
import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
class InvokeOneEnsure implements AsInvoker {
    final AsRunner valuer;
    final Class sourceType;
    final String name;
    Method method;
    Class paramType;
    Class componentType;

    private InvokeOneEnsure(AsRunner paramValuer, Class sourceType, String name) {
        this.valuer = paramValuer;
        this.sourceType = sourceType;
        this.name = name;
    }

    final static InvokeOneEnsure of(AsRunner paramValuer, Class sourceType, String name) {
        return new InvokeOneEnsure(paramValuer, sourceType, name);
    }

    /**
     * 变长参数支持
     *
     * @param data
     * @param type
     * @return
     */
    public Method getMethod(Object data, final Class type) {
        Method m = this.method;
        if (m == null || !paramType.isInstance(data)) {
            m = this.method = getMethod(type);
        }
        return m;
    }

    private Method getMethod(final Class type) {
        Method method;
        List<Method> methods = getPublicStaticMethods(
            sourceType, name, type);
        if (methods.size() > 0 && (method=methods.get(0)).getName().equals(name)) {
            paramType = type;
            return method;
        }
        if (methods.isEmpty()) {
            Class type0 = ArraysEnum.OBJECTS.TYPE;
            methods = getPublicStaticMethods(
                sourceType, name, type0);
            if (methods.size() > 0) {
                paramType = type0;
                componentType = Object.class;
                return methods.get(0);
            }
        }
        if (methods.isEmpty()) {
            Class type1 = Array.newInstance(type, 0).getClass();
            methods = getPublicStaticMethods(
                sourceType, name, type1);
            if (methods.size() > 0) {
                componentType = type;
                return methods.get(0);
            }
        }
        if (methods.isEmpty()) {
            methods = FilterUtil.filter(getPublicStaticMethods(sourceType, name),
                method0 -> {
                    Class[] classes = method0.getParameterTypes();
                    if (classes.length == 1) {
                        Class clazz = classes[0];
                        return clazz.isArray()
                            && clazz.getComponentType().isAssignableFrom(type);
                    }
                    return false;
                }, new ArrayList<>());
            if (methods.size() > 0) {
                componentType = type.getComponentType();
                return methods.get(0);
            }
        }
        throw new IllegalArgumentException("Can not find method of: " + sourceType + "(" + name + ")");

    }

    public Object createParams(Object data, Class type) {
        if (paramType == type) {
            return data;
        } else if (paramType == ArraysEnum.OBJECTS.TYPE) {
            return new Object[]{data};
        } else if (componentType.isAssignableFrom(type)) {
            Object arr = Array.newInstance(componentType, 1);
            Array.set(arr, 0, data);
            return arr;
        } else {
            throw new IllegalArgumentException("Unknown type of: " + type + "(" + data + ")");
        }
    }

    @Override
    public Object run(Object data) {
        data = valuer.run(data);
        Class type = data.getClass();
        return MethodUtil.invokeStatic(getMethod(data, type), createParams(data, type));
    }
}
