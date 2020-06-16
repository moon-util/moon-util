package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.RowFactory;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
final class TableDftOnly<T> extends TableCol {

    private final TransformForGet transformer;
    private final Predicate tester;
    private final T defaultVal;
    private final boolean always;

    TableDftOnly(AttrConfig config, TransformForGet defaultSetter, T defaultVal, boolean always, Predicate tester) {
        super(config);
        this.always = always;
        this.tester = tester;
        this.defaultVal = defaultVal;
        this.transformer = defaultSetter;
    }

    @Override
    final void render(IntAccessor indexer, RowFactory rowFactory, Object data) {
        if (data == null) {
            if (always) {
                CellFactory factory = toCellFactory(rowFactory, indexer);
                transformer.doTransform(factory, defaultVal);
            } else {
                skip(indexer);
            }
        } else {
            CellFactory factory = toCellFactory(rowFactory, indexer);
            Object propertyValue = getControl().control(data);
            if (tester.test(propertyValue)) {
                transformer.doTransform(factory, defaultVal);
            } else {
                getTransform().doTransform(factory, propertyValue);
            }
        }
    }
}
