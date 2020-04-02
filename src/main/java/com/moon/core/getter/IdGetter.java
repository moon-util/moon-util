package com.moon.core.getter;

import com.moon.core.util.interfaces.IdSupplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface IdGetter extends IdSupplier<String>, Getter {

    /**
     * get id
     *
     * @return
     */
    @Override
    String getId();
}
