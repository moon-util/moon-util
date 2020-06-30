package com.moon.spring.data;

import com.moon.more.data.registry.LayerEnum;
import com.moon.more.model.id.IdSupplier;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
public abstract class DataAccessorImpl<ID, T extends IdSupplier<ID>> extends BaseAccessorImpl<ID, T>
    implements DataAccessor<ID, T> {

    protected DataAccessorImpl(LayerEnum accessLay) { super(accessLay); }

    protected DataAccessorImpl(LayerEnum accessLay, Class domainClass) {
        super(accessLay, domainClass);
    }

    protected DataAccessorImpl(LayerEnum accessLay, LayerEnum registryMeLay) {
        super(accessLay, registryMeLay);
    }

    protected DataAccessorImpl(Class accessServeClass, LayerEnum registryMeLay) {
        super(accessServeClass, registryMeLay);
    }

    protected DataAccessorImpl(Class accessServeClass, LayerEnum registryMeLay, Class domainClass) {
        super(accessServeClass, registryMeLay, domainClass);
    }

    protected DataAccessorImpl(
        Class accessServeClass, LayerEnum accessLay, LayerEnum registryMeLay
    ) {
        super(accessServeClass, accessLay, registryMeLay);
    }

    protected DataAccessorImpl(
        Class accessServeClass, LayerEnum accessLay, LayerEnum registryMeLay, Class domainClass
    ) {
        super(accessServeClass, accessLay, registryMeLay, domainClass);
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
