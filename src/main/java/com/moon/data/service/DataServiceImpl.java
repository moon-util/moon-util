package com.moon.data.service;

import com.moon.data.BaseAccessor;
import com.moon.data.DataAccessorImpl;
import com.moon.data.Record;
import com.moon.data.registry.LayerEnum;

/**
 * @author moonsky
 */
public abstract class DataServiceImpl<T extends Record<String>> extends DataAccessorImpl<String, T>
    implements DataService<T> {

    protected DataServiceImpl() { this(null); }

    protected DataServiceImpl(Class<? extends BaseAccessor<String, T>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected DataServiceImpl(Class<? extends BaseAccessor<String, T>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum pullingThisLayer() { return LayerEnum.SERVICE; }

    @Override
    protected LayerEnum pullingAccessLayer() { return LayerEnum.REPOSITORY; }
}
