package com.moon.data.web;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
public abstract class DataLongController<T extends Record<Long>> extends DataController<T, Long> {

    protected DataLongController() { super(); }

    protected DataLongController(Class<? extends BaseAccessor<T, Long>> accessServeClass) {
        super(accessServeClass);
    }

    protected DataLongController(
        Class<? extends BaseAccessor<T, Long>> accessServeClass, Class<T> domainClass
    ) { super(accessServeClass, domainClass); }
}
