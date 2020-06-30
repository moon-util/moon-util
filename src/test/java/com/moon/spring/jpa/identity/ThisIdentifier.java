package com.moon.spring.jpa.identity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * @author moonsky
 */
public class ThisIdentifier implements IdentifierGenerator {

    private int start;
    private int end;

    public ThisIdentifier(int start, int end) {
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
        final StringBuilder sb = new StringBuilder("ThisIdentifier{");
        sb.append("start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }
}
