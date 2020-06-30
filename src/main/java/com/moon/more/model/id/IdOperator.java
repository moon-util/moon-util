package com.moon.more.model.id;

/**
 * @author moonsky
 */
public interface IdOperator<T> extends IdSupplier<T> {

    /**
     * 获取 ID
     *
     * @return
     */
    @Override
    T getId();

    /**
     * 设置 ID
     *
     * @param value
     */
    void setId(T value);
}
