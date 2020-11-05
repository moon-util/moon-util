package com.moon.mapping;

/**
 * 暂未实现
 *
 * @author moonsky
 */
interface ObjectMapping<THIS> {

    /**
     * 对象按各属性执行{@code toString()}方法
     *
     * @param thisObject 待执行{@code toString}对象
     *
     * @return 对象按属性 toString 后的子串形式
     */
    String toString(THIS thisObject);
}
