package com.moon.data;

import com.moon.core.model.supplier.IdSupplier;
import org.hibernate.annotations.Where;

import java.io.Serializable;

/**
 * @author moonsky
 */
public interface Record<ID> extends IdSupplier<ID>, Cloneable, Serializable {
    /**
     * @see Where#clause()
     */
    String WHERE_IDX = RecordConst.WHERE_IDX;
    /**
     * @see Where#clause()
     */
    String WHERE_STR = RecordConst.WHERE_STR;

    /**
     * 是否是新对象
     *
     * @return
     */
    default boolean isNew() { return getId() == null; }

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
    void setId(ID value);
}
