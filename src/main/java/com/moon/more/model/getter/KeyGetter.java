package com.moon.more.model.getter;

import com.moon.core.util.interfaces.KeySupplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface KeyGetter extends KeySupplier<String>, Getter {

    /**
     * get key
     *
     * @return
     */
    @Override
    String getKey();
}
