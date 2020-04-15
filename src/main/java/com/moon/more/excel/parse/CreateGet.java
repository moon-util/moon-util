package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataIndexer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
class CreateGet implements Creator<DefinedGet> {

    final static CreateGet CREATOR = new CreateGet();

    private CreateGet() { }

    static CreateGet getInstance() { return CREATOR; }

    @Override
    public Method get(PropertyDescriptor descriptor) {
        return descriptor.getReadMethod();
    }

    @Override
    public Type getGenericType(Method method) {
        return method.getGenericReturnType();
    }

    @Override
    public DefinedGet info(String propertyName, Marked<Method> onMethod) {
        return DefinedGet.of(propertyName, onMethod);
    }

    @Override
    public DefinedGet info(String propertyName, DataIndexer indexer) {
        return DefinedGet.of(propertyName, indexer);
    }

    @Override
    public ParsedDetail parsed(
        List list, ParsedRootDetail root, DefinedGet starting, DefinedGet ending
    ) { return ParsedDetail.ofGetter(list, root, starting, ending); }
}
