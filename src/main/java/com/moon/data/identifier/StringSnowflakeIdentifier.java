package com.moon.data.identifier;

import com.moon.data.IdentifierGenerator;

/**
 * @author moonsky
 */
public class StringSnowflakeIdentifier implements IdentifierGenerator<String, Object> {

    private final LongSnowflakeIdentifier identifier;

    public StringSnowflakeIdentifier() {
        this.identifier = new LongSnowflakeIdentifier();
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public StringSnowflakeIdentifier(long workerId, long datacenterId) {
        this.identifier = LongSnowflakeIdentifier.of(workerId, datacenterId);
    }

    /**
     * 生成 ID
     *
     * @param entity 数据实体
     * @param o      元数据，在 JPA 中，通常是指{@link SharedSessionContractImplementor}
     *
     * @return id
     */
    @Override
    public String generateId(Object entity, Object o) {
        return IdentifierUtil.returnIfRecordIdNotEmpty(entity,o, this::apply);
    }

    private String apply(Object e, Object i) {
        return identifier.originGenerateId(e, i).toString();
    }
}
