package com.moon.data.identifier;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import com.moon.data.IdentifierGenerator;
import com.moon.data.Record;
import com.moon.data.exception.UnknownIdentifierTypeException;
import com.moon.data.jpa.JpaRecord;
import com.moon.data.jpa.factory.AbstractRepositoryImpl;
import com.moon.data.jpa.factory.RepositoryBuilder;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public final class IdentifierUtil {

    private final static String packageName = IdentifierUtil.class.getPackage().getName();

    private final static Map<Class, RepositoryBuilder> REGISTERED_IDENTIFIER_TYPED_REPOSITORY_BUILDER_MAP = new ConcurrentHashMap<>();

    private final static Set<Class> USED_IDENTIFIER_TYPES = new HashSet<>();

    private IdentifierUtil() { ThrowUtil.noInstanceError(); }

    private static void assertNot(String classname, Class<?> type) {
        if (type.getName().equals(classname)) {
            throw new IllegalStateException("不允许：" + classname);
        }
    }

    private static String assertClassname(String classname) {
        assertNot(classname, IdentifierUtil.class);
        return classname;
    }

    private static Class toClassOrNull(String classname) {
        return ClassUtil.forNameOrNull(assertClassname(classname));
    }

    private static Class toIdentifierClass(String classname) {
        // 类全名
        Class type = toClassOrNull(classname);
        if (type == null) {
            // 类简写
            classname = packageName + "." + classname;
            type = toClassOrNull(classname);
        }
        if (type == null) {
            // 省略后缀的简写
            classname = classname + "Identifier";
            type = toClassOrNull(classname);
        }
        return Objects.requireNonNull(type);
    }

    /*
     * **********************************************************************************************
     * * default methods ****************************************************************************
     * **********************************************************************************************
     */

    static <T> T returnIfRecordIdNotNull(Object entity, Object o, BiFunction<Object, Object, T> generator) {
        if (entity instanceof Record) {
            Object id = ((Record<?>) entity).getId();
            if (id != null) {
                return (T) id;
            }
        }
        return generator.apply(entity, o);
    }

    static String returnIfRecordIdNotEmpty(Object entity, Object o, BiFunction<Object, Object, String> generator) {
        if (entity instanceof Record) {
            Object id = ((Record<?>) entity).getId();
            if (id instanceof CharSequence) {
                String strId = id.toString();
                if (!strId.isEmpty()) {
                    return strId;
                }
            }
        }
        return generator.apply(entity, o);
    }


    /*
     * **********************************************************************************************
     * * public methods *****************************************************************************
     * **********************************************************************************************
     */

    /**
     * 默认实现: 仅支持主键时 Long 或 String 类型
     *
     * @param <T> 实体类型
     */
    private final static class DefaultRepositoryImpl<T extends JpaRecord<Serializable>>
        extends AbstractRepositoryImpl<T, Serializable> {

        public DefaultRepositoryImpl(JpaEntityInformation ei, EntityManager em) { super(ei, em); }

        public DefaultRepositoryImpl(Class domainClass, EntityManager em) { super(domainClass, em); }
    }

    /**
     * 默认 Builder
     */
    private final static RepositoryBuilder BUILDER = DefaultRepositoryImpl::new;

    public static synchronized JpaRepositoryImplementation newRepositoryByIdentifierType(
        JpaEntityInformation information, EntityManager em
    ) {
        Class identifierClass = information.getIdType();
        USED_IDENTIFIER_TYPES.add(identifierClass);
        Map<Class, RepositoryBuilder> builderMap = REGISTERED_IDENTIFIER_TYPED_REPOSITORY_BUILDER_MAP;
        try {
            return builderMap.getOrDefault(identifierClass, BUILDER).newRepository(information, em);
        } catch (NullPointerException e) {
            throw new UnknownIdentifierTypeException("未知主键数据类型: " + identifierClass +//
                ", 支持的类型有: \n\t" + JoinerUtil.join(builderMap.keySet(), "\n\t"));
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
    ) { REGISTERED_IDENTIFIER_TYPED_REPOSITORY_BUILDER_MAP.put(identifierClass, repositoryBuilder); }

    /**
     * 创建 id 生成器
     *
     * @param description 描述
     * @param key         spring properties key
     *
     * @return ID 生成器
     */
    public static IdentifierGenerator newIdentifierGenerator(String description, String key) {
        // 默认雪花算法
        if (StringUtil.isBlank(description)) {
            Set<Class> types = USED_IDENTIFIER_TYPES;
            if (types.size() == 1) {
                Class type = SetUtil.requireGet(types, 0);
                if (type == Long.class) {
                    return new LongSnowflakeIdentifier();
                }
                if (type == String.class) {
                    return new StringSnowflakeIdentifier();
                }
            }
            throw new IllegalStateException("Unknown identifier type, you must assign spring property of: " + key);
        }
        // 其他方式主键策略
        String[] descriptions = description.split(":");
        Class type = toIdentifierClass(descriptions[0]);
        final int length = descriptions.length;
        if (length == 1) {
            return (IdentifierGenerator) ConstructorUtil.newInstance(type);
        }
        final int argsLen = length - 1;
        TypeCaster caster = TypeUtil.cast();
        for (Constructor constructor : type.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == argsLen) {
                try {
                    Object[] args = new Object[argsLen];
                    Class[] types = constructor.getParameterTypes();
                    for (int i = 0; i < argsLen; i++) {
                        Class argType = types[i];
                        String param = descriptions[i + 1];
                        args[i] = caster.toType(param, argType);
                    }
                    return (IdentifierGenerator) ConstructorUtil.newInstance(type, args);
                } catch (Throwable ignored) {
                }
            }
        }
        throw new IllegalStateException(description);
    }
}
