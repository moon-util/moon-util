package com.moon.mapper;

/**
 * @author benshaoye
 */
public interface BeanConverter<THIS, THAT> extends BeanMapper<THIS, THAT> {

    /**
     * 以当前类为数据源，构造并复制属性到目标对象
     *
     * @param thisObject 当前类实例，属性数据源
     *
     * @return {@code THAT}类实例
     *
     * @see #unsafeForward(Object, Object)
     */
    default THAT doForward(THIS thisObject) {
        // return thisObject == null ? null : unsafeForward(thisObject, new <THAT>());
        throw new UnsupportedOperationException("unknown target type of: doForward(Object, Object)");
    }

    /**
     * 以对象{@code thatObject}为数据源，构造并复制属性到当前类实例对象
     *
     * @param thatObject 属性数据源对象
     *
     * @return {@code THIS}类实例
     *
     * @see #unsafeBackward(Object, Object)
     */
    default THIS doBackward(THAT thatObject) {
        // return thatObject == null ? null : unsafeBackward(new <THIS>(), thatObject);
        throw new UnsupportedOperationException("unknown target type of: doBackward(Object, Object)");
    }
}
