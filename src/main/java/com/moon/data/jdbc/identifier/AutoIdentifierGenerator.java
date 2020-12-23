package com.moon.data.jdbc.identifier;

import com.moon.data.IdentifierGenerator;

import java.io.Serializable;

/**
 * 自增主键策略
 *
 * @author benshaoye
 */
public enum AutoIdentifierGenerator implements IdentifierGenerator {
    ;

    @Override
    public Serializable generateId(Object entity, Object o) { throw new UnsupportedOperationException(); }
}
