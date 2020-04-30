package com.moon.spring.jpa.identity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;

/**
 * @author benshaoye
 */
public class TimestampOrderedLongIdentifier extends TimestampOrderedStringIdentifier {

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return Long.valueOf(super.generate(session, object).toString());
    }
}
