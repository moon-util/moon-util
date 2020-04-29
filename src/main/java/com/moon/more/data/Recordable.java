package com.moon.more.data;

import com.moon.more.model.BaseSupporter;
import com.moon.more.model.id.IdOperator;
import com.moon.more.model.id.IdSupplier;

import java.io.Serializable;

/**
 * @author benshaoye
 */
public interface Recordable<ID> extends IdOperator<ID>, IdSupplier<ID>, BaseSupporter, Cloneable, Serializable {

    /**
     * @see DataRecordable#getAvailable()
     */
    String WHERE_IDX = " available=0 ";

    /**
     * @see DataRecordable#getAvailable()
     */
    String WHERE_STR = " available='YES' ";

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
