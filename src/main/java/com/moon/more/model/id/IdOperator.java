package com.moon.more.model.id;

/**
 * @author benshaoye
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
