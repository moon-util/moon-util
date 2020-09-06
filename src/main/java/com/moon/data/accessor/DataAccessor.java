package com.moon.data.accessor;

import com.moon.data.Record;

/**
 * @author moonsky
 */
public interface DataAccessor<T extends Record<ID>, ID> extends BaseAccessor<T, ID> {

    /**
     * 逻辑删除
     *
     * @param id ID
     */
    void disableById(ID id);

    /**
     * 逻辑删除
     *
     * @param entity 将要删除的对象
     */
    void disable(T entity);

    /**
     * 逻辑删除
     *
     * @param entities 将要删除的对象
     */
    void disableAll(Iterable<? extends T> entities);

    /**
     * 逻辑删除
     *
     * @param first    将要删除的对象
     * @param second   将要删除的对象
     * @param entities 将要删除的对象
     * @param <S>      对象子类
     */
    <S extends T> void disableAll(S first, S second, S... entities);
}
