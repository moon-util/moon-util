package com.moon.core.model;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface LongSortable extends Sortable<Long> {

    /**
     * 序号
     *
     * @return
     */
    @Override
    Long getSortValue();
}
