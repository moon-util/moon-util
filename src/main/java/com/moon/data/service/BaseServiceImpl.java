package com.moon.data.service;

import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.BaseAccessorImpl;
import com.moon.data.Record;
import com.moon.data.registry.LayerEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
@Transactional
@SuppressWarnings("all")
public abstract class BaseServiceImpl<T extends Record<String>> extends BaseAccessorImpl<String, T>
    implements BaseService<T> {

    protected BaseServiceImpl() { this(null); }

    protected BaseServiceImpl(Class<? extends BaseAccessor<String, T>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected BaseServiceImpl(Class<? extends BaseAccessor<String, T>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum pullingThisLayer() { return LayerEnum.SERVICE; }

    @Override
    protected LayerEnum pullingAccessLayer() { return LayerEnum.REPOSITORY; }
}
