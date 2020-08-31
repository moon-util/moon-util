package com.moon.data;

import com.moon.core.model.supplier.IdOperator;

import java.io.Serializable;

/**
 * @author moonsky
 */
public interface Record<ID> extends IdOperator<ID>, Cloneable, Serializable {

    String WHERE_IDX = RecordConst.WHERE_IDX;

    String WHERE_STR = RecordConst.WHERE_STR;

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
