package com.moon.data.synchronizer;

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
public abstract class BaseStringSynchronizerImpl<T extends Record<String>> extends BaseAccessorImpl<T, String>
    implements BaseStringSynchronizer<T> {

    protected BaseStringSynchronizerImpl() { this(null); }

    protected BaseStringSynchronizerImpl(Class<? extends BaseAccessor<T, String>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected BaseStringSynchronizerImpl(Class<? extends BaseAccessor<T, String>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.SYNCHRONIZER; }
}
