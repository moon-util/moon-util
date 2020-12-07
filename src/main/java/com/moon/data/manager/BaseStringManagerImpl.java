package com.moon.data.manager;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.BaseAccessorImpl;
import com.moon.data.registry.LayerEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
@Transactional(rollbackFor = RuntimeException.class)
public abstract class BaseStringManagerImpl<T extends Record<String>> extends BaseAccessorImpl<T, String>
    implements BaseStringManager<T> {

    protected BaseStringManagerImpl() { this(null); }

    protected BaseStringManagerImpl(Class<? extends BaseAccessor<T, String>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected BaseStringManagerImpl(Class<? extends BaseAccessor<T, String>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.MANAGER; }
}
