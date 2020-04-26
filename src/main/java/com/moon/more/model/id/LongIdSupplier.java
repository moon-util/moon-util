package com.moon.more.model.id;

/**
 * @author benshaoye
 */
public interface LongIdSupplier extends IdSupplier<Long> {

    /**
     * get an id
     *
     * @return
     */
    @Override
    Long getId();
}
