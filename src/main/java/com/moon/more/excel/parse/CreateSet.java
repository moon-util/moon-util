package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataIndexer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
class CreateSet implements Creator<DefinedSet> {

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
    public DefinedSet info(String propertyName, Marked<Method> onMethod) {
        return DefinedSet.of(propertyName, onMethod);
    }

    @Override
    public DefinedSet info(String propertyName, DataIndexer indexer) {
        return DefinedSet.of(propertyName, indexer);
    }

    @Override
    public Detail parsed(
        List list, DetailRoot root, DefinedSet starting, DefinedSet ending
    ) { return Detail.ofSetter(list, root, starting, ending); }
}
