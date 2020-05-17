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

    PropertiesGroup parsed(List list, RowRecord root, T rootProperty);

    T info(String propertyName, Annotated<Method> onMethod);

    default T info(String propertyName) { return info(propertyName, (Annotated<Method>) null); }

    T info(String propertyName, TableIndexer indexer);

    static Classes.CreateGet asGetter() { return Classes.CreateGet.getInstance(); }

    static Classes.CreateSet asSetter() { return Classes.CreateSet.getInstance(); }
}
