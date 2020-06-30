package com.moon.more.model.sort;

/**
 * @author moonsky
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
