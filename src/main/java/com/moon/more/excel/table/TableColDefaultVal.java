package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.annotation.DefaultValue;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
final class TableColDefaultVal extends TableCol {

    private final Predicate tester;
    private final String defaultValue;

    TableColDefaultVal(AttrConfig config, DefaultValue defaulter) {
        super(config);
        this.tester = TableColDefaultTester.valueOf(defaulter.defaultFor().name());
        this.defaultValue = defaulter.value();
    }

    @Override
    void render(IntAccessor indexer, RowFactory factory, Object data) {
        CellFactory cellFactory = toCellFactory(factory, indexer);
        if (data == null) {
            TransformForGet.DEFAULT.doTransform(cellFactory, defaultValue);
        } else {
            Object fieldVal = getControl().control(data);
            if (tester.test(fieldVal)) {
                // todo 数字等更精确的类型支持
                TransformForGet.DEFAULT.doTransform(cellFactory, defaultValue);
            } else {
                getTransform().doTransform(cellFactory, fieldVal);
            }
        }
    }
}
