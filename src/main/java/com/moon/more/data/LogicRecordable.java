package com.moon.more.data;

import com.moon.core.enums.Available;

/**
 * @author benshaoye
 */
public interface LogicRecordable<ID> extends Recordable<ID> {


    /**
     * 获取可用值
     *
     * @return
     */
    default Available getAvailable() { return null; }

    /**
     * 设置可用值
     *
     * @param available
     */
    default void setAvailable(Available available) { }

    /**
     * 文本
     *
     * @return
     */
    default String getAvailableText() { return obtainIfNonNull(getAvailable(), Available::getText); }

    /**
     * 是否可用
     *
     * @return
     */
    default boolean isDeleted() { return getAvailable() == Available.NO;}

    /**
     * 可用
     */
    default void withAvailable() { setAvailable(Available.YES); }

    /**
     * 不可用
     */
    default void withUnavailable() { setAvailable(Available.NO); }
}
