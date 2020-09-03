package com.moon.data.web;

import com.moon.core.lang.StringUtil;
import com.moon.data.Record;
import com.moon.data.registry.RecordRegistry;
import com.moon.data.registry.RecordRegistryException;
import com.moon.data.registry.LayerEnum;
import com.moon.data.accessor.BaseAccessorImpl;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.service.BaseService;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public abstract class BaseController<T extends Record<String>> extends BaseAccessorImpl<String, T> {

    protected BaseController() { this(null); }

    protected BaseController(Class<? extends BaseAccessor<String, T>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected BaseController(Class<? extends BaseAccessor<String, T>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @PostConstruct
    public void postConstruct() {
        Class domainClass = this.getDomainClass();
        if (domainClass != null) {
            try {
                registryVo2Entity(domainClass);
            } catch (RecordRegistryException e) {
                // ignore
            }
        }
    }

    @Override
    protected BaseAccessor<String, T> getDefaultAccessor() { return getService(); }

    @Override
    protected LayerEnum pullingThisLayer() { return LayerEnum.CONTROLLER; }

    @Override
    protected LayerEnum pullingAccessLayer() { return LayerEnum.SERVICE; }

    /**
     * 目标服务
     *
     * @return
     */
    protected BaseService<T> getService() {
        BaseAccessor accessor = getAccessor();
        if (accessor instanceof BaseService) {
            return (BaseService<T>) accessor;
        }
        return null;
    }


    /* registry -------------------------------------------------------- */

    protected final <T> void registryVo2Entity(Class<T> type) {
        registryVo2Entity(type, () -> {
            try {
                return type.newInstance();
            } catch (Exception e) {
                throw new BaseSettingsException("不能创建实例：" + type);
            }
        });
    }

    protected final <T> void registryVo2Entity(Supplier<T> defaultValueSupplier) {
        registryVo2Entity(domainClass, defaultValueSupplier);
    }

    protected final <T> void registryVo2Entity(Class<T> type, Supplier<T> defaultValueSupplier) {
        registryVo2Entity(type, defaultValueSupplier, this::getService);
    }

    protected final <T> void registryVo2Entity(
        Class<T> type, Supplier<T> defaultEntitySupplier, Supplier<? extends BaseService> serviceSupplier
    ) {
        RecordRegistry.registry(type, id -> {
            if (StringUtil.isEmpty(id)) {
                return defaultEntitySupplier.get();
            } else {
                Optional optional = serviceSupplier.get().findById(id);
                return optional.orElseGet(defaultEntitySupplier);
            }
        });
    }

    protected final T getOrNewEntityById(String id) { return getByRegistered(id); }

    protected final <E> E getByRegistered(String id) { return (E) getByRegistered(domainClass, id); }

    protected static final <E> E getByRegistered(Class<E> type, String id) {
        return RecordRegistry.getByRegistered(type, id);
    }
}
