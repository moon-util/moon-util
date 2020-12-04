package com.moon.mapping.processing;

/**
 * @author moonsky
 */
interface MappingBuilder {

    /**
     * 是否支持当前类型的转换
     *
     * @param manager manager，主要使用 manager 当前指向的属性
     *
     * @return 返回 {@code true}时会继续执行{@link #doMapping(Manager)}
     */
    boolean support(Manager manager);

    /**
     * 具体映射语句
     *
     * @param manager manager，主要使用 manager 当前指向的属性以及周边方法
     *
     * @return 当前所指向属性的单向映射语句，由{@link PropertyModel}控制映射方向
     */
    String doMapping(Manager manager);
}
