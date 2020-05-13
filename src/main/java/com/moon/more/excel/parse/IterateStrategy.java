package com.moon.more.excel.parse;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
interface IterateStrategy extends Predicate<Class> {

    IterateStrategy NONE = IterateFactory.NONE;

    Iterator iterator(Object data);

    /**
     * 顶级类，如{@link Iterable}、{@link java.util.Map}等
     * <p>
     * 用于判断集合类型
     *
     * @return
     */
    Class getTopClass();

    @Override
    default boolean test(Class propertyClass) { return getTopClass().isAssignableFrom(propertyClass); }
}
