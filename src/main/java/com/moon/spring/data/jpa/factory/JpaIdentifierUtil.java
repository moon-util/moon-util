package com.moon.spring.data.jpa.factory;

import com.moon.core.lang.JoinerUtil;
import com.moon.data.exception.UnknownIdentifierTypeException;
import com.moon.data.identifier.IdentifierUtil;
import com.moon.spring.data.jpa.JpaRecord;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author moonsky
 */
public final class JpaIdentifierUtil {

    private final static Map<Class, RepositoryBuilder> IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY = new ConcurrentHashMap<>();

    /**
     * 默认实现: 仅支持主键是 Long 或 String 类型
     *
     * @param <T> 实体类型
     */
    private final static class DefaultRepositoryImpl<T extends JpaRecord<Serializable>>
        extends AbstractRepositoryImpl<T, Serializable> {

        public DefaultRepositoryImpl(JpaEntityInformation ei, EntityManager em) { super(ei, em); }
    }

    @SuppressWarnings("all")
    public static JpaRepositoryImplementation newRepositoryByIdentifierType(
        JpaEntityInformation information, EntityManager em
    ) {
        Class identifierClass = information.getIdType();
        IdentifierUtil.addUsedIdentifierType(identifierClass);
        Map<Class, RepositoryBuilder> registry = IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY;
        try {
            return registry.getOrDefault(identifierClass, DefaultRepositoryImpl::new).newRepository(information, em);
        } catch (NullPointerException e) {
            throw new UnknownIdentifierTypeException("未知主键类型: " + identifierClass +//
                ", 支持的类型有: \n\t" + JoinerUtil.join(registry.keySet(), "\n\t"));
        }
    }

    /**
     * 注册
     *
     * @param identifierClass
     * @param repositoryBuilder
     */
    public static void registerIdentifierTypedRepositoryBuilder(
        Class identifierClass, RepositoryBuilder repositoryBuilder
    ) { IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY.put(identifierClass, repositoryBuilder); }
}
