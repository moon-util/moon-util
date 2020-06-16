package com.moon.more.excel.table;

import com.moon.core.util.SetUtil;
import com.moon.more.excel.annotation.DefaultNumber;
import com.moon.more.excel.annotation.DefaultValue;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.function.Predicate;

import static com.moon.more.excel.table.TransformForGet.DOUBLE;
import static com.moon.more.excel.table.TransformForGet.STRING;

/**
 * @author benshaoye
 */
final class TableDft {

    static TableCol of(AttrConfig config, DefaultNumber defaulter) {
        boolean always = defaulter.defaultForNullObj();
        Class<? extends Predicate> tester = defaulter.testBy();
        if (tester != Predicate.class) {
            return getTableCol(config, DOUBLE, defaulter.value(), always, tester);
        } else {
            DefaultNumber.Strategy[] strategies = defaulter.when();
            if (strategies.length > 1) {
                Set<DefaultNumber.Strategy> strategySet = SetUtil.newTreeSet(strategies);
                Predicate[] arr = SetUtil.toArray(strategySet, Predicate[]::new);
                return new TableDftList(config, DOUBLE, defaulter.value(), always, arr);
            } else if (strategies.length == 1) {
                return new TableDftOnly<>(config, DOUBLE, defaulter.value(), always, strategies[0]);
            } else {
                return new TableCol(config);
            }
        }
    }

    static TableCol of(AttrConfig config, DefaultValue defaulter) {
        boolean always = defaulter.defaultForNullObj();
        Class tester = defaulter.testBy();
        if (tester != Predicate.class) {
            return getTableCol(config, STRING, defaulter.value(), always, tester);
        } else {
            return new TableDftOnly<>(config, STRING, defaulter.value(), always, defaulter.when());
        }
    }

    private static <T> TableCol getTableCol(
        AttrConfig config, TransformForGet defaultSetter, T defaultVal, boolean always, Class tester
    ) {
        ParserUtil.checkValidImplClass(tester, Predicate.class);
        if (ParserUtil.isExpectCached(tester)) {
            try {
                Constructor<? extends Predicate> constructor = tester.getConstructor();
                return new TableDftOnly<>(config, defaultSetter, defaultVal, always, constructor.newInstance());
            } catch (Exception e) {
                throw new IllegalStateException("类：" + tester + " 应包含一个有效的公共无参构造方法", e);
            }
        } else if (tester.getDeclaringClass() == config.getTargetClass()) {
            try {
                Constructor<? extends Predicate> constructor = tester.getConstructor(config.getTargetClass());
                return new TableDftEach<>(config, defaultSetter, defaultVal, always, constructor);
            } catch (Exception e) {
                throw new IllegalStateException("类：" + tester + " 应包含一个有效的公共无参构造方法", e);
            }
        } else {
            throw new IllegalStateException("无法创建对象：" + tester);
        }
    }
}
