package com.moon.spring.data.jpa.id;

import com.moon.core.lang.MoonKey;
import com.moon.core.lang.ref.LazyAccessor;
import com.moon.data.IdentifierGenerator;
import com.moon.data.identifier.IdentifierUtil;
import com.moon.spring.SpringUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;

/**
 * id 生成器
 *
 * @author moonsky
 */
public class RecordIdentifierGenerator extends IdentifierUtil implements org.hibernate.id.IdentifierGenerator {

    private final LazyAccessor<IdentifierGenerator<? extends Serializable, Object>> accessor = LazyAccessor.of(() -> {
        String key = MoonKey.Data.Jpa.IDENTIFIER;
        String value = SpringUtil.getProperty(key);
        return IdentifierUtil.newIdentifierGenerator(value, key);
    });

    @Override
    public Serializable generate(
        SharedSessionContractImplementor session, Object object
    ) throws HibernateException {
        return returnIfRecordIdNotEmpty(object, session, (entity, s) -> accessor.get().generateId(entity, s));
    }
}
