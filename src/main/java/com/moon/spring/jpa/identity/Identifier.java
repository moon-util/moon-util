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

    private final LazyAccessor<IdentifierGenerator> accessor;

    public Identifier() {
        this.accessor = LazyAccessor.of(() -> {
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
                System.err.println("初始化 ID 生成器错误: " + e);
                return new SnowflakeIdentifier();
            }
        });
    }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        Serializable generatedId = accessor.get().generate(session, object);
        return generatedId;
    }
}
