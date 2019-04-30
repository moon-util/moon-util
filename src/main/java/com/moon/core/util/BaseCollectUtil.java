package com.moon.core.util;

import com.moon.core.enums.Collects;

import java.util.Collection;

/**
 * @author benshaoye
 * @date 2018/9/12
 */
class BaseCollectUtil {

    /*
     * ---------------------------------------------------------------------------------
     * adders
     * ---------------------------------------------------------------------------------
     */

    static <E, C extends Collection<E>> Collection concat0(C collect, C... cs) {
        Collection collection = Collects.getAsDeduce(collect).apply(collect);
        for (C c : cs) {
            collection.addAll(c);
        }
        return collection;
    }
}
