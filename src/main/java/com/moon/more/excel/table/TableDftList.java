package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.RowFactory;

import java.util.function.Predicate;

/**
 * @author benshaoye
 */
final class TableDftList<T> extends TableCol {

    private final GetTransformer transformer;
    private final Predicate[] testers;
    private final T defaultVal;
    private final boolean always;

    TableDftList(AttrConfig config, TransformForGet defaultSetter, T defaultVal, boolean always, Predicate... testers) {
        super(config);
        this.always = always;
        this.testers = testers;
        this.defaultVal = defaultVal;
        this.transformer = defaultSetter;
    }

    private boolean test(Object propertyValue) {
        Predicate[] testers = this.testers;
        for (Predicate tester : testers) {
            if (tester.test(propertyValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    final void render(IntAccessor indexer, RowFactory rowFactory, Object data) {
        if (data == null) {
            if (always) {
                CellFactory factory = toCellFactory(rowFactory, indexer);
                transformer.doTransform(factory, defaultVal);
            } else {
                skip(rowFactory, indexer);
            }
        } else {
            CellFactory factory = toCellFactory(rowFactory, indexer);
            Object propertyValue = getControl().control(data);
            if (test(propertyValue)) {
                transformer.doTransform(factory, defaultVal);
            } else {
                getTransform().doTransform(factory, propertyValue);
            }
        }
    }
}
