package com.moon.data.service;

import com.moon.data.DataAccessorImpl;
import com.moon.data.Record;
import com.moon.data.registry.LayerEnum;

/**
 * @author moonsky
 */
public abstract class DataServiceImpl<T extends Record<String>> extends DataAccessorImpl<String, T>
    implements DataService<T> {

    protected DataServiceImpl() { this(null); }

    protected DataServiceImpl(Class accessType) { super(accessType, null); }

    protected DataServiceImpl(Class accessType, Class domainClass) {
        super(accessType, LayerEnum.REPOSITORY, LayerEnum.SERVICE, domainClass);
    }

    protected DataServiceImpl(LayerEnum accessLay, Class domainClass) {
        this(accessLay, LayerEnum.SERVICE, domainClass);
    }

    protected DataServiceImpl(LayerEnum accessLay, LayerEnum registryLay, Class domainClass) {
        super(null, accessLay, registryLay, domainClass);
    }

    protected DataServiceImpl(
        Class accessServeClass, LayerEnum accessLay, LayerEnum registryMeLay, Class domainClass
    ) { super(accessServeClass, accessLay, registryMeLay, domainClass); }
}
