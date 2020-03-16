package com.moon.core.model;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface IntSortable extends Sortable<Integer> {

    /**
     * 序号
     *
     * @return
     */
    @Override
    Integer getSortValue();
}
