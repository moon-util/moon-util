package com.moon.more.excel.table;

import com.moon.more.excel.annotation.FieldTransformer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author benshaoye
 */
abstract class TableColTransform extends TableCol {


    private final GetTransformer transformer;

    TableColTransform(AttrConfig config, Class<FieldTransformer> transformerCls) {
        super(config);

        GetTransformer transformer;
        Type superType = transformerCls.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) superType;
            Class actualCls = (Class) type.getActualTypeArguments()[1];
            transformer = TransformForGet.findOrDefault(actualCls);
        } else {
            transformer = ((factory, value) -> {
                if (factory != null) {
                    TransformForGet.findOrDefault(value.getClass()).doTransform(factory, value);
                }
            });
        }
        this.transformer = transformer;
    }

    @Override
    protected GetTransformer getTransform() { return transformer; }
}
