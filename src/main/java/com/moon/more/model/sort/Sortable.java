package com.moon.more.model.sort;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Sortable<T> {

    /**
     * 序号
     *
     * @return
     */
    T getSortValue();
}
