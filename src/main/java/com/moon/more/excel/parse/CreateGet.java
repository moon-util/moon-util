package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableIndexer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
class CreateGet implements Creator<PropertyGet> {

    final static CreateGet CREATOR = new CreateGet();

    private CreateGet() { }

    static CreateGet getInstance() { return CREATOR; }

    @Override
    public Method get(PropertyDescriptor descriptor) { return descriptor.getReadMethod(); }

    @Override
    public Type getGenericType(Method method) { return method.getGenericReturnType(); }

    @Override
    public PropertyGet info(String propertyName, Marked<Method> onMethod) {
        return PropertyGet.of(propertyName, onMethod);
    }

    @Override
    public PropertyGet info(String propertyName, TableIndexer indexer) { return PropertyGet.of(propertyName, indexer); }

    @Override
    public PropertiesGroup parsed(
        List list, DetailRoot root, PropertyGet starting, PropertyGet ending
    ) { return PropertiesGroup.ofGetter(list, root, starting, ending); }
}
