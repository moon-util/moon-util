package com.moon.data.web;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
public abstract class BaseStringController<T extends Record<String>> extends BaseController<T, String> {

    protected BaseStringController() { super(); }

    protected BaseStringController(Class<? extends BaseAccessor<T, String>> accessServeClass) {
        super(accessServeClass);
    }

    protected BaseStringController(
        Class<? extends BaseAccessor<T, String>> accessServeClass, Class<T> domainClass
    ) { super(accessServeClass, domainClass); }
}
