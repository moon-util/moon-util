package com.moon.more.model.sort;

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
