package com.moon.data.web;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
public abstract class BaseLongController<T extends Record<Long>> extends BaseController<T, Long> {

    protected BaseLongController() { super(); }

    protected BaseLongController(Class<? extends BaseAccessor<T, Long>> accessServeClass) {
        super(accessServeClass);
    }

    protected BaseLongController(
        Class<? extends BaseAccessor<T, Long>> accessServeClass, Class<T> domainClass
    ) { super(accessServeClass, domainClass); }
}
