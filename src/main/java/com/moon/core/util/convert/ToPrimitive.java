package com.moon.core.util.convert;

import java.util.Set;

/**
 * @author moonsky
 */
final class ToPrimitive implements Converts {

    private final Set<Class<?>> supportFrom;
    private final Set<Class<?>> supportTo;
    private final Class supportToClass;
    private final Converts wrappedConvert;
    private final Object defaultIfNull;

    ToPrimitive(Set<Class<?>> supportFrom, Class<?> supportTo, Converts wrappedConvert, Object defaultIfNull) {
        this.supportFrom = supportFrom;
        this.supportTo = ToUtil.unmodifiableHashSet(supportTo);
        this.wrappedConvert = wrappedConvert;
        this.defaultIfNull = defaultIfNull;
        this.supportToClass = supportTo;
    }

    /**
     * 执行转换
     *
     * @param o 数据源
     *
     * @return 转换后的值
     */
    @Override
    public Object convert(Object o) {
        Object value = wrappedConvert.convert(o);
        return value == null ? defaultIfNull : value;
    }

    /**
     * 支持转换成什么数据类型
     *
     * @return 目标数据类型集合
     */
    @Override
    public Set<Class<?>> supportsTo() {
        return supportTo;
    }

    /**
     * 支持从什么数据类型转换
     *
     * @return 数据类型来源
     */
    @Override
    public Set<Class<?>> supportsFrom() {
        return supportFrom;
    }

    @Override
    public boolean supportsTo(Class type) {
        return supportToClass == type;
    }
}
