package com.moon.data.jpa;

import com.moon.data.Available;
import com.moon.data.DataRecord;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
public interface JpaDataRecord<ID> extends DataRecord<ID>, JpaRecord<ID> {

    /**
     * 获取可用值
     *
     * @return
     */
    @Override
    default Available getAvailable() { return Available.YES; }

    /**
     * 设置可用值
     *
     * @param available
     */
    @Override
    void setAvailable(Available available);

    /**
     * 文本
     *
     * @return
     */
    @Override
    default String getAvailableText() {
        return getAvailable() == null ? null : getAvailable().getText();
    }

    /**
     * 是否已删除
     *
     * @return
     */
    @Override
    default boolean isDeleted() { return getAvailable() == Available.NO; }

    /**
     * 可用
     */
    @Override
    default void withAvailable() { setAvailable(Available.YES); }

    /**
     * 不可用
     */
    @Override
    default void withUnavailable() { setAvailable(Available.NO); }

    /**
     * Returns if the {@code Persistable} is new or was persisted already.
     *
     * @return if {@literal true} the object is new.
     */
    @Override
    default boolean isNew() { return getId() == null; }

    /**
     * 获取 ID
     * <p>
     * 当使用{@link Long}做主键时，注意前后端数字的兼容性
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

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    @Override
    default Supplier<ID> asIdSupplier() { return this::getId; }
}
