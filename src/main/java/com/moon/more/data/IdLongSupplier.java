package com.moon.more.data;

/**
 * @author benshaoye
 */
public interface IdLongSupplier extends IdentitySupplier<Long> {

    /**
     * get an id
     *
     * @return
     */
    @Override
    Long getId();
}
