package com.moon.spring.jpa.id;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.LazyAccessor;
import com.moon.spring.SpringUtil;
import com.moon.spring.SpringKeyConst;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * id 生成器
 * @author moonsky
 */
public class Identifier implements IdentifierGenerator {

    private final LazyAccessor<IdentifierGenerator> accessor;

    private static IdentifierGenerator asDefault() {
        return new TimestampOrderedStringIdentifier();
    }

    public Identifier() {
        this.accessor = LazyAccessor.of(() -> {
            String value = SpringUtil.getProperty(SpringKeyConst.Jpa.IDENTIFIER);
            if (StringUtil.isNotBlank(value)) {
                try {
                    return IdentifierUtil.newInstance(value);
                } catch (Throwable t) {
                    throw new IllegalStateException(value);
                }
            } else {
                return asDefault();
            }
        });
    }

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return accessor.get().generate(session, object);
    }
}
