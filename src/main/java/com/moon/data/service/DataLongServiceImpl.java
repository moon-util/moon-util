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
public abstract class DataLongServiceImpl<T extends Record<Long>> extends DataAccessorImpl<T, Long>
    implements DataLongService<T> {

    protected DataLongServiceImpl() { this(null); }

    protected DataLongServiceImpl(Class<? extends BaseAccessor<T, Long>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected DataLongServiceImpl(Class<? extends BaseAccessor<T, Long>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.SERVICE; }

    @Override
    protected LayerEnum pullingAccessLayer() { return LayerEnum.REPOSITORY; }
}
