package com.moon.data.accessor;

import com.moon.data.Record;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
@Transactional
@SuppressWarnings("all")
public abstract class DataAccessorImpl<T extends Record<ID>, ID> extends BaseAccessorImpl<T, ID>
    implements DataAccessor<T, ID> {

    /**
     * 构造器
     *
     * @param accessServeClass 将要访问的服务具体实现类型，如：UserServiceImpl
     * @param domainClass      具体实体类型
     */
    protected DataAccessorImpl(Class<? extends BaseAccessor<T, ID>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected DataAccessor<T, ID> getAccessor() { return (DataAccessor<T, ID>) super.getAccessor(); }

    @Override
    protected DataAccessor<T, ID> getDefaultAccessor() { return null; }

    /**
     * 逻辑删除
     *
     * @param id
     */
    @Override
    @Transactional
    public void disableById(ID id) { getAccessor().disableById(id); }

    /**
     * 逻辑删除
     *
     * @param entity
     */
    @Override
    @Transactional
    public void disable(T entity) { getAccessor().disable(entity); }

    /**
     * 逻辑删除
     *
     * @param entities
     */
    @Override
    @Transactional
    public void disableAll(Iterable<? extends T> entities) { getAccessor().disableAll(entities); }

    /**
     * 逻辑删除
     *
     * @param first
     * @param second
     * @param entities
     */
    @Override
    @Transactional
    public <S extends T> void disableAll(S first, S second, S... entities) {
        getAccessor().disableAll(first, second, entities);
    }
}
