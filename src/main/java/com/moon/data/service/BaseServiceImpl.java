package com.moon.data.service;

import com.moon.data.BaseAccessorImpl;
import com.moon.data.Record;
import com.moon.data.registry.LayerEnum;

/**
 * @author moonsky
 */
public abstract class BaseServiceImpl<T extends Record<String>> extends BaseAccessorImpl<String, T>
    implements BaseService<T> {

    protected BaseServiceImpl() { this(null); }

    protected BaseServiceImpl(Class accessType) { super(accessType, null); }

    protected BaseServiceImpl(Class accessType, Class domainClass) {
        super(accessType, LayerEnum.REPOSITORY, LayerEnum.SERVICE, domainClass);
    }

    protected BaseServiceImpl(LayerEnum accessLay, Class domainClass) {
        this(accessLay, LayerEnum.SERVICE, domainClass);
    }

    protected BaseServiceImpl(LayerEnum accessLay, LayerEnum thisLay, Class domainClass) {
        super(null, accessLay, thisLay, domainClass);
    }

    protected BaseServiceImpl(
        Class accessServeClass, LayerEnum accessLay, LayerEnum registryMeLay, Class domainClass
    ) { super(accessServeClass, accessLay, registryMeLay, domainClass); }
}
