package com.moon.mapping;

/**
 * 暂未实现
 *
 * @author moonsky
 */
public interface ObjectMapping<THIS> {

    /**
     * 对象按各属性执行{@code toString()}方法
     *
     * @param thisObject 待执行{@code toString}对象
     *
     * @return 对象按属性 toString 后的子串形式
     */
    default String toString(THIS thisObject) { return String.valueOf(thisObject); }

    /**
     * 浅复制一个当前对象
     *
     * @param thisObject 将要复制的对象
     *
     * @return thisObject 的副本
     */
    THIS clone(THIS thisObject);
}
