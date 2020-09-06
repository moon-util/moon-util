package com.moon.data.jpa.repository;

import com.moon.data.accessor.DataAccessor;
import com.moon.data.jpa.JpaRecord;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
@NoRepositoryBean
public interface DataRepository<T extends JpaRecord<ID>, ID> extends BaseRepository<T, ID>, DataAccessor<T, ID> {

    /**
     * 逻辑删除
     *
     * @param id ID
     */
    @Override
    void disableById(ID id);

    /**
     * 逻辑删除
     *
     * @param entity 将要删除的对象
     */
    @Override
    void disable(T entity);

    /**
     * 逻辑删除
     *
     * @param entities 将要删除的对象
     */
    @Override
    void disableAll(Iterable<? extends T> entities);

    /**
     * 逻辑删除
     *
     * @param first    将要删除的对象
     * @param second   将要删除的对象
     * @param entities 将要删除的对象
     */
    @Override
    <S extends T> void disableAll(S first, S second, S... entities);
}
