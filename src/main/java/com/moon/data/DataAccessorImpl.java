package com.moon.data;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
public abstract class DataAccessorImpl<ID, T extends Record<ID>> extends BaseAccessorImpl<ID, T>
    implements DataAccessor<ID, T> {

    /**
     * 构造器
     *
     * @param accessServeClass 将要访问的服务具体实现类型，如：UserServiceImpl
     * @param domainClass      具体实体类型
     */
    protected DataAccessorImpl(Class<? extends BaseAccessor<ID, T>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected DataAccessor<ID, T> getAccessor() { return (DataAccessor<ID, T>) super.getAccessor(); }

    @Override
    protected DataAccessor<ID, T> getDefaultAccessor() { return null; }

    /**
     * 逻辑删除
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void disableById(ID id) { getAccessor().disableById(id); }

    /**
     * 逻辑删除
     *
     * @param entity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void disable(T entity) { getAccessor().disable(entity); }

    /**
     * 逻辑删除
     *
     * @param entities
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void disableAll(Iterable<? extends T> entities) { getAccessor().disableAll(entities); }

    /**
     * 逻辑删除
     *
     * @param first
     * @param second
     * @param entities
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public <S extends T> void disableAll(S first, S second, S... entities) {
        getAccessor().disableAll(first, second, entities);
    }
}
