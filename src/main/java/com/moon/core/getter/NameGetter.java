package com.moon.core.getter;

import com.moon.core.util.interfaces.NameSupplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface NameGetter extends NameSupplier<String> {

    /**
     * get name
     *
     * @return
     */
    @Override
    String getName();
}
