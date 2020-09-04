package com.moon.data.jpa.id;

import com.moon.data.IdentifierGenerator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author moonsky
 */
public class ProxiedIdentifierGenerator implements org.hibernate.id.IdentifierGenerator,
                                                   InvocationHandler,
                                                   IdentifierGenerator<Serializable, SharedSessionContractImplementor> {

    private IdentifierGenerator identifier;

    public ProxiedIdentifierGenerator(IdentifierGenerator identifier) {
        this.identifier = identifier;
    }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException { return generateId(object, session); }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return generateId(args[1], (SharedSessionContractImplementor) args[0]);
    }

    /**
     * 生成 ID
     *
     * @param entity  数据对象
     * @param session 元数据
     *
     * @return id
     */
    @Override
    public Serializable generateId(
        Object entity, SharedSessionContractImplementor session
    ) { return identifier.generateId(entity, session); }
}
