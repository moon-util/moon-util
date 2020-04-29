package com.moon.spring.jpa.identity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author benshaoye
 */
public class UUIDIdentifier implements IdentifierGenerator {

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return UUID.randomUUID().toString();
    }
}
