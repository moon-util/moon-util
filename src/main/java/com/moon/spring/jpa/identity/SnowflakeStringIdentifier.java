package com.moon.spring.jpa.identity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;

/**
 * @author moonsky
 */
public class SnowflakeStringIdentifier extends SnowflakeLongIdentifier {

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) {
        return String.valueOf(super.generate(session, object));
    }
}
