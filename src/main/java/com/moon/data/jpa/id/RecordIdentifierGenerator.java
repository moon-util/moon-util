package com.moon.data.jpa.id;

import com.moon.core.lang.Moon;
import com.moon.core.lang.ref.LazyAccessor;
import com.moon.core.lang.reflect.ProxyUtil;
import com.moon.data.IdentifierGenerator;
import com.moon.data.identifier.IdentifierUtil;
import com.moon.spring.SpringUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;

/**
 * id 生成器
 *
 * @author moonsky
 */
public class RecordIdentifierGenerator implements org.hibernate.id.IdentifierGenerator {

    private final LazyAccessor<org.hibernate.id.IdentifierGenerator> accessor;

    public RecordIdentifierGenerator() {
        this.accessor = LazyAccessor.of(() -> {
            String value = SpringUtil.getProperty(Moon.Data.Jpa.IDENTIFIER);
            IdentifierGenerator identifier = IdentifierUtil.newInstance(value);
            return ProxyUtil.newProxyInstance(new ProxiedIdentifierGenerator(identifier), org.hibernate.id.IdentifierGenerator.class);
        });
    }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return accessor.get().generate(session, object);
    }
}
