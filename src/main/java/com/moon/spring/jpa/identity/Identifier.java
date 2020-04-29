package com.moon.spring.jpa.identity;

import com.moon.core.lang.StringUtil;
import com.moon.spring.ContextUtil;
import com.moon.spring.MoonConst;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.Serializable;

/**
 * @author benshaoye
 */
public class Identifier implements IdentifierGenerator {

    private final IdentifierGenerator generator;

    public Identifier() {
        ApplicationContext context = ContextUtil.getContext();
        Environment env = context.getEnvironment();
        String value = env.getProperty(MoonConst.Data.IDENTIFIER);
        if (StringUtil.isNotBlank(value)) {
            try {
                this.generator = IdentifierUtil.newInstance(value);
            } catch (Throwable t) {
                throw new IllegalStateException(value);
            }
        } else {
            this.generator = new SnowflakeIdentifier();
        }
    }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return generator.generate(session, object);
    }
}
