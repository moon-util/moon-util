package com.moon.data.web;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
public abstract class DataStringController<T extends Record<String>> extends DataController<T, String> {

    protected DataStringController() { super(); }

    protected DataStringController(Class<? extends BaseAccessor<T, String>> accessServeClass) {
        super(accessServeClass);
    }

    protected DataStringController(
        Class<? extends BaseAccessor<T, String>> accessServeClass, Class<T> domainClass
    ) { super(accessServeClass, domainClass); }
}
