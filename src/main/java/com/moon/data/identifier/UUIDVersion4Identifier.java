package com.moon.data.identifier;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.moon.core.dep.Dependencies;
import com.moon.core.lang.ThrowUtil;
import com.moon.data.IdentifierGenerator;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public class UUIDVersion4Identifier implements IdentifierGenerator<String, Object> {

    private final TimeBasedGenerator generator;

    public UUIDVersion4Identifier() {
        Object generator;
        try {
            generator = Generators.randomBasedGenerator();
        } catch (NoClassDefFoundError e) {
            throw Dependencies.UUID_GENERATOR.getException();
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                throw Dependencies.UUID_GENERATOR.getException();
            }
            generator = ThrowUtil.runtime(e);
        }
        this.generator = (TimeBasedGenerator) generator;
    }

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
        return generator.generate().toString();
    }
}
