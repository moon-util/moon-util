package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableIndexer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
interface Creator<T extends Property> {

    Method get(PropertyDescriptor descriptor);

    Type getGenericType(Method method);

    PropertiesGroup parsed(List list, DetailRoot root, T starting, T ending);

    T info(String propertyName, Marked<Method> onMethod);

    default T info(String propertyName) { return info(propertyName, (Marked<Method>) null); }

    T info(String propertyName, TableIndexer indexer);

    static CreateGet asGetter() { return CreateGet.getInstance(); }

    static CreateSet asSetter() { return CreateSet.getInstance(); }
}
