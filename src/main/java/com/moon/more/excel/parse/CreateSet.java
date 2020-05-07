package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableIndexer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
class CreateSet implements Creator<PropertySet> {

    final static CreateSet CREATOR = new CreateSet();

    private CreateSet() { }

    static CreateSet getInstance() { return CREATOR; }

    @Override
    public Method get(PropertyDescriptor descriptor) { return descriptor.getWriteMethod(); }

    @Override
    public Type getGenericType(Method method) {
        throw new UnsupportedOperationException("setter 方法暂未支持");
    }

    @Override
    public PropertySet info(String propertyName, Marked<Method> onMethod) {
        return PropertySet.of(propertyName, onMethod);
    }

    @Override
    public PropertySet info(String propertyName, TableIndexer indexer) {
        return PropertySet.of(propertyName, indexer);
    }

    @Override
    public PropertiesGroup parsed(
        List list, DetailRoot root, PropertySet starting, PropertySet ending
    ) { return PropertiesGroup.ofSetter(list, root, starting, ending); }
}
