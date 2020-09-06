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
     * @return 如果当前对象是新创建的对象，返回 true，否则返回 false
     */
    default boolean isNew() { return getId() == null; }

    /**
     * 获取 ID
     * <p>
     * 当使用{@link Long}做主键时，注意前后端数字的兼容性
     *
     * @return 当前对象的 id 或 null
     */
    @Override
    ID getId();

    /**
     * 设置 ID
     *
     * @param value id 值
     */
    void setId(ID value);
}
