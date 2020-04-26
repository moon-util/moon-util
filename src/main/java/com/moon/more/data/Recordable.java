package com.moon.more.data;

import com.moon.more.model.BaseSupporter;
import com.moon.more.model.id.IdOperator;
import com.moon.more.model.id.IdSupplier;

/**
 * @author benshaoye
 */
public interface Recordable<ID> extends IdOperator<ID>, IdSupplier<ID>, BaseSupporter, Cloneable {

    /**
     * 是否是新对象
     *
     * @return
     */
    boolean isNew();
}
