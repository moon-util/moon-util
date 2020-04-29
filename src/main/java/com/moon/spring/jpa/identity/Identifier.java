package com.moon.spring.jpa.identity;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.LazyAccessor;
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

    private final LazyAccessor<IdentifierGenerator> generator;

    public Identifier() {
        this.generator = LazyAccessor.of(() -> {
            try {
                ApplicationContext context = ContextUtil.getContext();
                Environment env = context.getEnvironment();
                String value = env.getProperty(MoonConst.Data.IDENTIFIER);
                if (StringUtil.isNotBlank(value)) {
                    try {
                        return IdentifierUtil.newInstance(value);
                    } catch (Throwable t) {
                        throw new IllegalStateException(value);
                    }
                } else {
                    return new SnowflakeIdentifier();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                return new SnowflakeIdentifier();
            }
        });
    }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return generator.get().generate(session, object);
    }
}
