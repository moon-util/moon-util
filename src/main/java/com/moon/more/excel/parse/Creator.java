package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataIndexer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
interface Creator<T extends Defined> {

    Method get(PropertyDescriptor descriptor);

    Type getGenericType(Method method);

    ParsedDetail parsed(List list, ParsedRootDetail root, T starting, T ending);

    T info(String propertyName, Marked<Method> onMethod);

    default T info(String propertyName) {
        return info(propertyName, (Marked<Method>) null);
    }

    T info(String propertyName, DataIndexer indexer);

    static CreateGet asGetter() {
        return CreateGet.getInstance();
    }

    static CreateSet asSetter() {
        return CreateSet.getInstance();
    }
}
