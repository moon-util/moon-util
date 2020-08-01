package com.moon.poi.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.SetUtil;
import com.moon.poi.excel.CellFactory;
import com.moon.poi.excel.RowFactory;
import com.moon.poi.excel.annotation.defaults.DefaultNumber;
import com.moon.poi.excel.annotation.defaults.DefaultValue;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
abstract class TableDft extends TableCol {

    private final GetTransfer defaulter;
    private final Object defaultVal;
    private final boolean always;
    private final boolean useSuperSetter;

    protected TableDft(AttrConfig config, GetTransfer transformer, Object defaultVal, boolean always) {
        super(config);
        this.always = always;
        this.defaultVal = defaultVal;
        this.defaulter = transformer;
        this.useSuperSetter = transformer == TransferForGet.DOUBLE || transformer == TransferForGet.NUMBER;
    }

    final boolean isAlways() { return always; }

    final void setMatchedVal(CellFactory factory) {
        if (useSuperSetter) {
            setNormalVal(factory, defaultVal);
        } else {
            defaulter.transfer(factory, defaultVal);
            applyClassname(factory);
        }
    }

    /**
     * 检测字段值是否符合默认值条件
     *
     * @param rowData       行数据
     * @param propertyValue 字段值
     *
     * @return 是否符合条件
     */
    abstract boolean isMatched(Object rowData, Object propertyValue);

    @Override
    final void render(IntAccessor indexer, RowFactory rowFactory, Object data) {
        if (data == null) {
            if (isAlways()) {
                setMatchedVal(indexedCell(rowFactory, indexer));
            } else {
                skip(rowFactory, indexer);
            }
        } else {
            CellFactory factory = indexedCell(rowFactory, indexer);
            Object propertyValue = getControl().control(data);
            if (isMatched(data, propertyValue)) {
                setMatchedVal(factory);
            } else {
                setNormalVal(factory, propertyValue);
            }
        }
    }

    @Override
    final void render(TableProxy proxy) {
        if (proxy.isSkipped()) {
            if (isAlways()) {
                setMatchedVal(proxy.indexedCell(getOffset(), isFillSkipped()));
            } else {
                proxy.skip(getOffset(), isFillSkipped());
            }
        } else {
            CellFactory factory = proxy.indexedCell(getOffset(), isFillSkipped());
            Object thisData = proxy.getThisData(getControl());
            if (isMatched(proxy.getRowData(), thisData)) {
                setMatchedVal(factory);
            } else {
                setNormalVal(factory, thisData);
            }
        }
    }

    final static TableCol of(AttrConfig config, DefaultNumber defaulter) {
        boolean always = defaulter.defaultForNullObj();
        Class<? extends Predicate> tester = defaulter.testBy();
        if (tester != Predicate.class) {
            return getTableCol(config, TransferForGet.DOUBLE, defaulter.value(), always, tester);
        } else {
            DefaultNumber.Strategy[] strategies = defaulter.when();
            if (strategies.length > 1) {
                Set<DefaultNumber.Strategy> strategySet = SetUtil.newTreeSet(strategies);
                Predicate[] arr = SetUtil.toArray(strategySet, Predicate[]::new);
                return new TableDftList(config, TransferForGet.DOUBLE, defaulter.value(), always, arr);
            } else if (strategies.length == 1) {
                return new TableDftOnly(config, TransferForGet.DOUBLE, defaulter.value(), always, strategies[0]);
            } else {
                return new TableCol(config);
            }
        }
    }

    final static TableCol of(AttrConfig config, DefaultValue defaulter) {
        boolean always = defaulter.defaultForNullObj();
        Class tester = defaulter.testBy();
        if (tester != Predicate.class) {
            return getTableCol(config, TransferForGet.STRING, defaulter.value(), always, tester);
        } else {
            return new TableDftOnly(config, TransferForGet.STRING, defaulter.value(), always, defaulter.when());
        }
    }

    private static TableCol getTableCol(
        AttrConfig config, TransferForGet defaultSetter, Object defaultVal, boolean always, Class tester
    ) {
        ParserUtil.checkValidImplClass(tester, Predicate.class);
        if (ParserUtil.isExpectCached(tester)) {
            try {
                Constructor<? extends Predicate> constructor = tester.getConstructor();
                return new TableDftOnly(config, defaultSetter, defaultVal, always, constructor.newInstance());
            } catch (Exception e) {
                throw new IllegalStateException("类：" + tester + " 应包含一个有效的公共无参构造方法", e);
            }
        } else if (tester.getDeclaringClass() == config.getTargetClass()) {
            try {
                Constructor<? extends Predicate> constructor = tester.getConstructor(config.getTargetClass());
                return new TableDftEach(config, defaultSetter, defaultVal, always, constructor);
            } catch (Exception e) {
                throw new IllegalStateException("类：" + tester + " 应包含一个有效的公共无参构造方法", e);
            }
        } else {
            throw new IllegalStateException("无法创建对象：" + tester);
        }
    }

    /**
     * 只有一个检查条件
     */
    private static final class TableDftOnly extends TableDft {

        private final Predicate tester;

        TableDftOnly(
            AttrConfig config, TransferForGet defaultSetter, Object defaultVal, boolean always, Predicate tester
        ) {
            super(config, defaultSetter, defaultVal, always);
            this.tester = tester;
        }

        @Override
        final boolean isMatched(Object rowData, Object propertyValue) { return tester.test(propertyValue); }
    }


    /**
     * 有多个检查条件
     */
    private static final class TableDftList extends TableDft {

        private final Predicate[] testers;

        TableDftList(
            AttrConfig config, TransferForGet defaultSetter, Object defaultVal, boolean always, Predicate... testers
        ) {
            super(config, defaultSetter, defaultVal, always);
            this.testers = testers;
        }

        @Override
        final boolean isMatched(Object rowData, Object propertyValue) {
            Predicate[] testers = this.testers;
            for (Predicate tester : testers) {
                if (tester.test(propertyValue)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 每次单独检查
     */
    private final static class TableDftEach extends TableDft {

        private final Constructor<Predicate> constructor;

        TableDftEach(
            AttrConfig config, TransferForGet defaultSetter, Object defaultVal, boolean always, Constructor constructor
        ) {
            super(config, defaultSetter, defaultVal, always);
            this.constructor = constructor;
        }

        private IllegalStateException newError(Exception e) {
            Class targetClass = constructor.getDeclaringClass();
            throw new IllegalStateException("类：" + targetClass

                + " 应该包含一个有效的公共无参构造方法", e);
        }

        private Predicate newTester(Object data) {
            try {
                return constructor.newInstance(data);
            } catch (Exception e) {
                throw newError(e);
            }
        }

        @Override
        final boolean isMatched(Object rowData, Object propertyValue) { return newTester(rowData).test(propertyValue); }
    }
}
