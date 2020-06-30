package com.moon.more.model.getter;

import com.moon.core.util.interfaces.ValueSupplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface ValueGetter extends ValueSupplier<String>, Getter {

    /**
     * get value
     *
     * @return
     */
    @Override
    String getValue();
}
