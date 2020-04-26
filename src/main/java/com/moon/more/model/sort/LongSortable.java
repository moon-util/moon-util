package com.moon.more.model.sort;

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
