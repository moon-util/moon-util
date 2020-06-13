package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;
import com.moon.more.excel.annotation.TableColumn;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
final class TableColDefaultVal extends TableCol {

    private final Predicate tester;
    private final String defaultValue;

    TableColDefaultVal(Attribute attr) {
        super(attr);
        TableColumn column = attr.getTableColumn();
        this.tester = TableColDefaultTester.valueOf(column.defaultFor().name());
        this.defaultValue = column.defaultValue();
    }

    @Override
    void render(CellFactory factory, Object data) {
        Object fieldVal = getControl().control(data);
        if (tester.test(fieldVal)) {
            TransformForGet.DEFAULT.doTransform(factory, defaultValue);
        } else {
            getTransform().doTransform(factory, fieldVal);
        }
    }
}
