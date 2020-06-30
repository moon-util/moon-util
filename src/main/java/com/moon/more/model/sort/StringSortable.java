package com.moon.more.model.sort;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface StringSortable extends Sortable<String> {

    /**
     * 序号
     *
     * @return
     */
    @Override
    String getSortValue();
}
