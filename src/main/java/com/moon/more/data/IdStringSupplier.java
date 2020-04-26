package com.moon.more.data;

/**
 * @author benshaoye
 */
public interface IdStringSupplier extends IdentitySupplier<String> {

    /**
     * get an id
     *
     * @return
     */
    @Override
    String getId();
}
