package com.moon.more.model.id;

/**
 * @author moonsky
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
