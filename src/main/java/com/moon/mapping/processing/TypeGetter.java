package com.moon.mapping.processing;

/**
 * @author benshaoye
 */
interface TypeGetter {

    /**
     * 属性的声明类型（可能是泛型）
     *
     * @return 声明类型
     */
    String getDeclareType();

    /**
     * 属性实际类型
     * <p>
     * 如果声明类型是泛型，这里就是对应的实际类型或泛型边界
     * <p>
     * 如果声明类型不是泛型，这里为 null
     *
     * @return 实际类型
     */
    String getActualType();

    /**
     * 返回属性的实际类型
     *
     * @return 计算后的实际类型
     */
    default String getComputedType() {
        return getActualType() == null ? getDeclareType() : getActualType();
    }
}
