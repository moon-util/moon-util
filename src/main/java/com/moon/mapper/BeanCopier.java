package com.moon.mapper;

/**
 * @author benshaoye
 */
public interface BeanCopier<THIS, THAT> {

    default THAT unsafeCopy(THIS thisObject, THAT thatObject) { return thatObject; }

    default THAT copy(THIS thisObject, THAT thatObject) {
        return (thisObject == null || thatObject == null) ? thatObject : unsafeCopy(thisObject, thatObject);
    }

    /**
     * 以当前类为数据源，构造并复制属性到目标对象
     *
     * @param thisObject 当前类实例，属性数据源
     *
     * @return {@code THAT}类实例
     *
     * @see #unsafeCopy(Object, Object)
     */
    default THAT convert(THIS thisObject) {
        // return thisObject == null ? null : unsafeForward(thisObject, new <THAT>());
        throw new UnsupportedOperationException("unknown target type of: doForward(Object, Object)");
    }
}
