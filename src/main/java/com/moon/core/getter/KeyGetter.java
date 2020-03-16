package com.moon.core.getter;

import com.moon.core.util.interfaces.KeySupplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface KeyGetter extends KeySupplier<String> {

    /**
     * get key
     *
     * @return
     */
    @Override
    String getKey();
}
