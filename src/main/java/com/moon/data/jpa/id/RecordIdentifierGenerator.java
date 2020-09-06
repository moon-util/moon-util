package com.moon.data.jpa.id;

import com.moon.core.lang.MoonKey;
import com.moon.core.lang.ref.LazyAccessor;
import com.moon.core.lang.reflect.ProxyUtil;
import com.moon.data.identifier.IdentifierUtil;
import com.moon.spring.SpringUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

/**
 * id 生成器
 *
 * @author moonsky
 */
public class RecordIdentifierGenerator implements IdentifierGenerator {

    private final LazyAccessor<IdentifierGenerator> accessor = LazyAccessor.of(() -> {
        String key = MoonKey.Data.Jpa.IDENTIFIER;
        String value = SpringUtil.getProperty(key);
        com.moon.data.IdentifierGenerator identifier = IdentifierUtil.newInstance(value, key);
        InvocationHandler handler = new ProxiedIdentifierGenerator(identifier);
        return ProxyUtil.newProxyInstance(handler, IdentifierGenerator.class);
    });

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return accessor.get().generate(session, object);
    }
}
