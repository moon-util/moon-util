package com.moon.spring.jpa.identity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * @author moonsky
 */
public class StringIdentifier implements IdentifierGenerator {

    private String start;
    private String end;

    public StringIdentifier(String start, String end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StringIdentifier{");
        sb.append("start='").append(start).append('\'');
        sb.append(", end='").append(end).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
