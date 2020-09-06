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
public abstract class BaseLongServiceImpl<T extends Record<Long>> extends BaseAccessorImpl<T, Long>
    implements BaseLongService<T> {

    protected BaseLongServiceImpl() { this(null); }

    protected BaseLongServiceImpl(Class<? extends BaseAccessor<T, Long>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected BaseLongServiceImpl(Class<? extends BaseAccessor<T, Long>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.SERVICE; }

    @Override
    protected LayerEnum pullingAccessLayer() { return LayerEnum.REPOSITORY; }
}
