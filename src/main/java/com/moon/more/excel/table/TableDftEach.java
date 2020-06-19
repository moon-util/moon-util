package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.RowFactory;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
final class TableDftEach<T> extends TableCol {

    private final TransformForGet transformer;
    private final Constructor<Predicate> constructor;
    private final T defaultVal;
    private final boolean always;

    TableDftEach(
        AttrConfig config, TransformForGet defaultSetter, T defaultVal, boolean always, Constructor constructor
    ) {
        super(config);
        this.always = always;
        this.defaultVal = defaultVal;
        this.constructor = constructor;
        this.transformer = defaultSetter;
    }

    private IllegalStateException newError(Exception e) {
        Class targetClass = constructor.getDeclaringClass();
        throw new IllegalStateException("类：" + targetClass + " 应该包含一个有效的公共无参构造方法", e);
    }

    private Predicate newTester(Object data) {
        try {
            return constructor.newInstance(data);
        } catch (Exception e) {
            throw newError(e);
        }
    }

    @Override
    void render(TableProxy proxy) {
        if (proxy.isSkipped()) {
            if (always) {
                CellFactory factory = proxy.indexedCell(getOffset(), isFillSkipped());
                transformer.doTransform(factory, defaultVal);
            } else {
                proxy.skip(getOffset(), isFillSkipped());
            }
        } else {
            CellFactory factory = proxy.indexedCell(getOffset(), isFillSkipped());
            Object thisData = proxy.getThisData(getControl());
            if (newTester(proxy.getRowData()).test(thisData)) {
                transformer.doTransform(factory, defaultVal);
            } else {
                getTransform().doTransform(factory, thisData);
            }
        }
    }

    @Override
    void render(IntAccessor indexer, RowFactory rowFactory, Object data) {
        if (data == null) {
            if (always) {
                CellFactory factory = indexedCell(rowFactory, indexer);
                transformer.doTransform(factory, defaultVal);
            } else {
                skip(rowFactory, indexer);
            }
        } else {
            CellFactory factory = indexedCell(rowFactory, indexer);
            Object propertyValue = getControl().control(data);
            if (newTester(data).test(propertyValue)) {
                transformer.doTransform(factory, defaultVal);
            } else {
                getTransform().doTransform(factory, propertyValue);
            }
        }
    }
}
