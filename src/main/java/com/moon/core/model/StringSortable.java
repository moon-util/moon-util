package com.moon.core.model;

/**
 * @author benshaoye
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
