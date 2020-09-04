package com.moon.data.accessor;

import com.moon.data.Record;

/**
 * @author moonsky
 */
public interface DataAccessor<T extends Record<ID>, ID> extends BaseAccessor<T, ID> {

    /**
     * 逻辑删除
     *
     * @param id
     */
    void disableById(ID id);

    /**
     * 逻辑删除
     *
     * @param entity
     */
    void disable(T entity);

    /**
     * 逻辑删除
     *
     * @param entities
     */
    void disableAll(Iterable<? extends T> entities);

    /**
     * 逻辑删除
     *
     * @param first
     * @param second
     * @param entities
     * @param <S>
     */
    <S extends T> void disableAll(S first, S second, S... entities);
}
