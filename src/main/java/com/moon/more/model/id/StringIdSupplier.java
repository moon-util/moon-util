package com.moon.more.model.id;

/**
 * @author benshaoye
 */
public interface StringIdSupplier extends IdSupplier<String> {

    /**
     * get an id
     *
     * @return
     */
    @Override
    String getId();
}
