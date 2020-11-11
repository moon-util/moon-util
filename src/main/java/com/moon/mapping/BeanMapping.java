package com.moon.mapping;

/**
 * @author moonsky
 */
public interface BeanMapping<THIS, THAT> extends MapMapping<THIS> {

    /**
     * 将当前对象{@code thisObject}按属性名映射到另一个对象{@code thatObject}
     *
     * @param thisObject 属性数据源对象
     * @param thatObject 属性映射目标对象
     *
     * @return 设置属性后的目标对象 thatObject
     */
    default THAT copyForward(THIS thisObject, THAT thatObject) {
        throw new UnsupportedOperationException("unknown target type of: toThat(Object, Object)");
    }

    /**
     * 从目标属性数据源{@code thatObject}按属性名称复制到当前对象{@code thisObject}
     *
     * @param thisObject 当前对象，属性映射目标对象
     * @param thatObject 属性数据源对象
     *
     * @return 当前对象 thisObject
     */
    default THIS copyBackward(THIS thisObject, THAT thatObject) {
        throw new UnsupportedOperationException("unknown target type of: fromThat(Object, Object)");
    }

    /**
     * 以对象{@code thatObject}为数据源，构造并复制属性到当前类实例对象
     *
     * @param thatObject 属性数据源对象
     *
     * @return 当前类实例
     */
    default THIS newThis(THAT thatObject) {
        throw new UnsupportedOperationException("unknown target type of: newThis(Object, Object)");
    }

    /**
     * 以当前类为数据源，构造并复制属性到目标对象
     *
     * @param thisObject 当前类实例，属性数据源
     *
     * @return 目标对象
     */
    default THAT newThat(THIS thisObject) {
        throw new UnsupportedOperationException("unknown target type of: newThat(Object, Object)");
    }
}
