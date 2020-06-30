package com.moon.more.model.id;

/**
 * @author moonsky
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
