package com.moon.spring.data.jpa.factory;

import com.moon.core.lang.JoinerUtil;
import com.moon.data.exception.UnknownIdentifierTypeException;
import com.moon.data.identifier.IdentifierUtil;
import com.moon.spring.data.jpa.JpaRecord;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class JpaIdentifierUtil extends IdentifierUtil {

    private JpaIdentifierUtil() { noInstanceError(); }

    /**
     * 缓存两种类型键值队
     * <p>
     * class    : Class  ==> repository builder Function
     * <p>
     * classname: String ==> repository implementation class
     */
    private final static Map IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY = new ConcurrentHashMap<>();

    final static <T extends JpaRecord<?>> T extractPresetPrimaryKey(T record) {
        return putRecordPresetPrimaryKey(record);
    }

    /**
     * 注册
     *
     * @param identifierClass
     * @param repositoryBuilder
     */
    public static void registerIdentifierTypedRepositoryBuilder(
        Class identifierClass, RepositoryBuilder repositoryBuilder, Class repositoryImplementationClass
    ) {
        IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY.put(identifierClass, repositoryBuilder);
        IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY.put(identifierClass.getName(), repositoryImplementationClass);
    }

    public static <ID, E extends JpaRecord<ID>, T extends JpaRepositoryImplementation<E, ID>> Class<T> getRepositoryImplementationClass(
        Class<ID> identifierClass, Class<T> defaultIfAbsent
    ) {
        return (Class) IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY.getOrDefault(identifierClass.getName(),
            defaultIfAbsent);
    }

    @SuppressWarnings("all")
    public static JpaRepositoryImplementation newTargetRepository(
        RepositoryInformation repositoryInformation,
        JpaEntityInformation entityInformation,
        EntityManager em,
        JpaRecordRepositoryMetadata metadata
    ) {
        Class identifierClass = repositoryInformation.getIdType();
        IdentifierUtil.addUsedIdentifierType(identifierClass);
        Map<Class, RepositoryBuilder> registry = IDENTIFIER_TYPED_REPOSITORY_BUILDER_REGISTRY;
        try {
            return registry.getOrDefault(identifierClass, DefaultRepositoryImpl::new)
                .newRepository(repositoryInformation, entityInformation, em, metadata);
        } catch (NullPointerException e) {
            throw new UnknownIdentifierTypeException("未知主键类型: " + identifierClass +//
                ", 支持的类型有: \n\t" + JoinerUtil.join(registry.keySet(), "\n\t"));
        }
    }

    /**
     * 默认实现
     *
     * @param <T> 实体类型
     */
    final static class DefaultRepositoryImpl<T extends JpaRecord<Serializable>>
        extends AbstractRepositoryImpl<T, Serializable> {

        public DefaultRepositoryImpl(
            RepositoryInformation repositoryInformation,
            JpaEntityInformation ei,
            EntityManager em,
            JpaRecordRepositoryMetadata metadata
        ) { super(repositoryInformation, ei, em, metadata); }
    }
}
