package com.moon.data;

import com.moon.data.registry.LayerEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
public abstract class DataAccessorImpl<ID, T extends Record<ID>> extends BaseAccessorImpl<ID, T>
    implements DataAccessor<ID, T> {

    protected DataAccessorImpl(LayerEnum accessLay) { super(accessLay); }

    protected DataAccessorImpl(LayerEnum accessLay, Class domainClass) {
        super(accessLay, domainClass);
    }

    protected DataAccessorImpl(LayerEnum accessLay, LayerEnum thisLay) {
        super(accessLay, thisLay);
    }

    protected DataAccessorImpl(Class accessServeClass, LayerEnum thisLay) {
        super(accessServeClass, thisLay);
    }

    protected DataAccessorImpl(Class accessServeClass, LayerEnum thisLay, Class domainClass) {
        super(accessServeClass, thisLay, domainClass);
    }

    protected DataAccessorImpl(
        Class accessServeClass, LayerEnum accessLay, LayerEnum thisLay
    ) {
        super(accessServeClass, accessLay, thisLay);
    }

    protected DataAccessorImpl(
        Class accessServeClass, LayerEnum accessLay, LayerEnum thisLay, Class domainClass
    ) {
        super(accessServeClass, accessLay, thisLay, domainClass);
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
