package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;
import com.moon.more.excel.annotation.DefaultValue;
import com.moon.more.excel.annotation.TableColumn;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
final class TableColDefaultVal extends TableCol {

    private final Predicate tester;
    private final String defaultValue;

    TableColDefaultVal(Attribute attr, DefaultValue defaulter) {
        super(attr);
        this.tester = TableColDefaultTester.valueOf(defaulter.defaultFor().name());
        this.defaultValue = defaulter.value();
    }

    @Override
    void render(CellFactory factory, Object data) {
        Object fieldVal = getControl().control(data);
        if (tester.test(fieldVal)) {
            // todo 数字的支持
            TransformForGet.DEFAULT.doTransform(factory, defaultValue);
        } else {
            getTransform().doTransform(factory, fieldVal);
        }
    }
}
