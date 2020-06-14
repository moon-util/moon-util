package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;
import com.moon.more.excel.annotation.FieldTransformer;

import java.lang.reflect.Constructor;

/**
 * @author benshaoye
 */
final class TableColTransformEvery extends TableColTransform {

    private final Constructor<FieldTransformer> constructor;
    private final Class transformerCls;

    TableColTransformEvery(
        Attribute attr, Class<FieldTransformer> transformerCls
    ) {
        super(attr, transformerCls);

        Class declareCls = transformerCls.getDeclaringClass();
        try {
            this.constructor = transformerCls.getDeclaredConstructor(declareCls);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("实现类「" + transformerCls + "」应有一个无参构造器", e);
        }
        this.transformerCls = transformerCls;
    }

    private FieldTransformer getFieldTransformer(Object data) {
        try {
            // todo 构造器不同访问权限导致的不能构造实例问题
            return constructor.newInstance(data);
        } catch (Exception e) {
            throw new IllegalStateException("Can not new instance of: " + transformerCls, e);
        }
    }

    @Override
    void render(CellFactory factory, Object data) {
        Object value = getControl().control(data);
        // todo TableColumnTransformer 返回值和原生字段类型不一致问题, 重写 getTransform()
        Object cellVal = getFieldTransformer(data).transform(value);
        getTransform().doTransform(factory, cellVal);
    }
}
