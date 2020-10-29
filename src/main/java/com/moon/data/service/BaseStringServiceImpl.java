package com.moon.data.service;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.BaseAccessorImpl;
import com.moon.data.registry.LayerEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
@Transactional
@SuppressWarnings("all")
public abstract class BaseStringServiceImpl<T extends Record<String>> extends BaseAccessorImpl<T, String>
    implements BaseStringService<T> {

    protected BaseStringServiceImpl() { this(null); }

    protected BaseStringServiceImpl(Class<? extends BaseAccessor<T, String>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected BaseStringServiceImpl(Class<? extends BaseAccessor<T, String>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.SERVICE; }
}
