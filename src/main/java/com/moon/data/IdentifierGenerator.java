package com.moon.data;


import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author moonsky
 */
public interface IdentifierGenerator<ID extends Serializable, METADATA> {

    /**
     * 生成 ID
     *
     * @param entity   数据实体
     * @param metadata 元数据，在 JPA 中，通常是指{@link SharedSessionContractImplementor}
     *
     * @return id
     */
    ID generateId(Object entity, METADATA metadata);

    /**
     * 生成 ID
     *
     * @param entity   数据对象
     * @param metadata 元数据
     *
     * @return id
     */
    default Optional<ID> getIdentifierAsOptional(Object entity, METADATA metadata) {
        return Optional.ofNullable(generateId(entity, metadata));
    }

    /**
     * 生成 ID
     *
     * @param entity   数据对象
     * @param metadata 元数据
     *
     * @return id
     */
    default ID getRequiredIdentifier(Object entity, METADATA metadata) {
        return Objects.requireNonNull(generateId(entity, metadata), "The identifier must not be 'null'.");
    }
}
