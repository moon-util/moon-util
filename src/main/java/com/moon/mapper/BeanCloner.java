package com.moon.mapper;

import com.moon.mapper.annotation.Cloner;

/**
 * 对象克隆器
 *
 * @author benshaoye
 * @see Cloner
 */
public interface BeanCloner<T> {

    /**
     * 执行 clone
     *
     * @param thisObject 源对象
     *
     * @return 克隆对象
     */
    T clone(T thisObject);

    /**
     * 深度克隆
     *
     * @param thisObject 待克隆对象
     *
     * @return 深度克隆的对象
     */
    T deepClone(T thisObject);
}
