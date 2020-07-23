package com.moon.more.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.core.enums.Available;

/**
 * @author moonsky
 */
public interface DataRecord<ID> extends Record<ID> {

    String WHERE_IDX = Available.WHERE_IDX;

    String WHERE_STR = Available.WHERE_STR;

    /**
     * 获取可用值
     *
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    default Available getAvailable() { return Available.YES; }

    /**
     * 设置可用值
     *
     * @param available
     */
    void setAvailable(Available available);

    /**
     * 文本
     *
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    default String getAvailableText() { return obtainIfNonNull(getAvailable(), Available::getText); }

    /**
     * 是否已删除
     *
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    default boolean isDeleted() { return getAvailable() == Available.NO; }

    /**
     * 可用
     */
    default void withAvailable() { setAvailable(Available.YES); }

    /**
     * 不可用
     */
    default void withUnavailable() { setAvailable(Available.NO); }
}
