package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableIndexer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
public class Classes {

    static class CreateGet implements Creator<PropertyGet> {

        final static CreateGet CREATOR = new CreateGet();

        private CreateGet() { }

        static CreateGet getInstance() { return CREATOR; }

        @Override
        public Method get(PropertyDescriptor descriptor) { return descriptor.getReadMethod(); }

        @Override
        public Type getGenericType(Method method) { return method.getGenericReturnType(); }

        @Override
        public PropertyGet info(String propertyName, Annotated<Method> onMethod) {
            return PropertyGet.of(propertyName, onMethod);
        }

        @Override
        public PropertyGet info(String propertyName, TableIndexer indexer) { return PropertyGet.of(propertyName, indexer); }

        @Override
        public PropertiesGroup parsed(
            List list, RowRecord root, PropertyGet rootProperty
        ) { return PropertiesGroup.ofGetter(list, root, rootProperty); }
    }

    static class CreateSet implements Creator<PropertySet> {

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
        public PropertySet info(String propertyName, Annotated<Method> onMethod) {
            return PropertySet.of(propertyName, onMethod);
        }

        @Override
        public PropertySet info(String propertyName, TableIndexer indexer) {
            return PropertySet.of(propertyName, indexer);
        }

        @Override
        public PropertiesGroup parsed(
            List list, RowRecord root, PropertySet rootProperty
        ) { return PropertiesGroup.ofSetter(list, root, rootProperty); }
    }
}
