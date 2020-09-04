package com.moon.data.service;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.DataAccessorImpl;
import com.moon.data.registry.LayerEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
@Transactional
@SuppressWarnings("all")
public abstract class DataStringServiceImpl<T extends Record<String>> extends DataAccessorImpl<T, String>
    implements DataStringService<T> {

    protected DataStringServiceImpl() { this(null); }

    protected DataStringServiceImpl(Class<? extends BaseAccessor<T, String>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected DataStringServiceImpl(Class<? extends BaseAccessor<T, String>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.SERVICE; }

    @Override
    protected LayerEnum pullingAccessLayer() { return LayerEnum.REPOSITORY; }
}
