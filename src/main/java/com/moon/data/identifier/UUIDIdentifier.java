package com.moon.data.identifier;

import com.moon.data.IdentifierGenerator;

import java.util.UUID;

/**
 * @author moonsky
 */
public class UUIDIdentifier implements IdentifierGenerator<String, Object> {

    /**
     * 生成 ID
     *
     * @param entity 数据对象
     * @param o      元数据
     *
     * @return id
     */
    @Override
    public String generateId(Object entity, Object o) {
        return IdentifierUtil.returnIfRecordIdNotEmpty(entity, o, this::apply);
    }

    private String apply(Object e, Object i) {
        return UUID.randomUUID().toString();
    }
}
