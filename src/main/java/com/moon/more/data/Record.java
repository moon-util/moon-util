package com.moon.more.data;

import com.moon.core.enums.Available;
import com.moon.more.model.BaseSupporter;
import com.moon.more.model.id.IdOperator;
import com.moon.more.model.id.IdSupplier;

import java.io.Serializable;

/**
 * @author moonsky
 */
public interface Record<ID> extends IdOperator<ID>, IdSupplier<ID>, BaseSupporter, Cloneable, Serializable {

    /**
     * @see DataRecord#getAvailable()
     */
    String WHERE_IDX = Available.WHERE_IDX;

    /**
     * @see DataRecord#getAvailable()
     */
    String WHERE_STR = Available.WHERE_STR;

    /**
     * 是否是新对象
     *
     * @return
     */
    boolean isNew();

    /**
     * 获取 ID
     *
     * @return
     */
    @Override
    ID getId();

    /**
     * 设置 ID
     *
     * @param value
     */
    @Override
    void setId(ID value);
}
