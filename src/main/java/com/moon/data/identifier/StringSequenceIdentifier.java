package com.moon.data.identifier;

import com.moon.data.IdentifierGenerator;

/**
 * @author moonsky
 */
public class StringSequenceIdentifier implements IdentifierGenerator<String, Object> {

    private final LongSequenceIdentifier identifier;

    public StringSequenceIdentifier() {
        this.identifier = new LongSequenceIdentifier();
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public StringSequenceIdentifier(long workerId, long datacenterId) {
        this.identifier = LongSequenceIdentifier.of(workerId, datacenterId);
    }

    /**
     * 生成 ID
     *
     * @param entity 数据实体
     * @param o      元数据，在 JPA 中，通常是指{@link org.hibernate.engine.spi.SharedSessionContractImplementor}
     *
     * @return id
     */
    @Override
    public String generateId(Object entity, Object o) {
        return originGenerateId(entity, o).toString();
    }

    protected final Long originGenerateId(Object e, Object i) {
        return identifier.originGenerateId(e, i);
    }
}
