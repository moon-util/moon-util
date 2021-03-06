package com.moon.mapping;

/**
 * @author moonsky
 */
public interface BeanMapper<THIS, THAT> {

    /**
     * null 不安全的属性映射，不会对{@code thisObject}和{@code thatObject}进行{@code null}检查
     * <p>
     * 将当前对象{@code thisObject}按属性名映射到另一个对象{@code thatObject}
     *
     * @param thisObject 属性数据源对象
     * @param thatObject 属性映射目标对象
     *
     * @return 设置属性后的目标对象 {@code thatObject}
     */
    default THAT unsafeForward(THIS thisObject, THAT thatObject) { return thatObject; }

    /**
     * null 不安全的属性映射，不会对{@code thisObject}和{@code thatObject}进行{@code null}检查
     * <p>
     * 从目标属性数据源{@code thatObject}按属性名称复制到当前对象{@code thisObject}
     *
     * @param thisObject 当前对象，属性映射目标对象
     * @param thatObject 属性数据源对象
     *
     * @return 当前对象 {@code thisObject}
     */
    default THIS unsafeBackward(THIS thisObject, THAT thatObject) { return thisObject; }

    /**
     * 将当前对象{@code thisObject}按属性名映射到另一个对象{@code thatObject}
     *
     * @param thisObject 属性数据源对象
     * @param thatObject 属性映射目标对象
     *
     * @return 设置属性后的目标对象 {@code thatObject}
     */
    default THAT doForward(THIS thisObject, THAT thatObject) {
        return (thisObject == null || thatObject == null) ? thatObject : unsafeForward(thisObject, thatObject);
    }

    /**
     * 从目标属性数据源{@code thatObject}按属性名称复制到当前对象{@code thisObject}
     *
     * @param thisObject 当前对象，属性映射目标对象
     * @param thatObject 属性数据源对象
     *
     * @return 当前对象 {@code thisObject}
     */
    default THIS doBackward(THIS thisObject, THAT thatObject) {
        return (thisObject == null || thatObject == null) ? thisObject : unsafeBackward(thisObject, thatObject);
    }
}
