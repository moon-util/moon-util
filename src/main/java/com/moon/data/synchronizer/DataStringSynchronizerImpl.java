package com.moon.data.synchronizer;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.DataAccessorImpl;
import com.moon.data.registry.LayerEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author moonsky
 */
@Transactional(rollbackFor = RuntimeException.class)
public abstract class DataStringSynchronizerImpl<T extends Record<String>> extends DataAccessorImpl<T, String>
    implements DataStringSynchronizer<T> {

    protected DataStringSynchronizerImpl() { this(null); }

    protected DataStringSynchronizerImpl(Class<? extends BaseAccessor<T, String>> accessServeClass) {
        this(accessServeClass, null);
    }

    protected DataStringSynchronizerImpl(Class<? extends BaseAccessor<T, String>> accessServeClass, Class<T> domainClass) {
        super(accessServeClass, domainClass);
    }

    @Override
    protected LayerEnum provideThisLayer() { return LayerEnum.SYNCHRONIZER; }
}
