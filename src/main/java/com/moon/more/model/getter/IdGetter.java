package com.moon.more.model.getter;

import com.moon.more.model.id.IdSupplier;

/**
 * @author moonsky
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
