package com.moon.spring.jpa.id;

import com.moon.core.lang.StringUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author moonsky
 */
public class TimestampOrderedStringIdentifier implements IdentifierGenerator {

    private final static int THRESHOLD = 94999;

    private final AtomicInteger adder = new AtomicInteger();

    public TimestampOrderedStringIdentifier() { }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        Date date = new Date();
        long timestamp = date.getTime();
        int index = adder.getAndIncrement();
        if (index > THRESHOLD) {
            synchronized (adder) {
                if (adder.get() > THRESHOLD) {
                    adder.set(0);
                }
            }
        }
        return timestamp + StringUtil.padStart(index, 6, '0');
    }
}
