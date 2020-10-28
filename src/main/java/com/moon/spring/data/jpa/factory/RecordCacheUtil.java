package com.moon.spring.data.jpa.factory;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.data.annotation.RecordCacheGroup;
import com.moon.data.annotation.RecordCacheable;
import com.moon.spring.data.jpa.start.EnableJpaRecordCaching;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @author moonsky
 */
final class RecordCacheUtil {

    /**
     * @see com.moon.spring.data.jpa.start.JpaRecordCacheRegistrar#CACHE_PROPERTIES_NAME
     */
    @SuppressWarnings("all")
    private final static String CACHE_PROPERTIES_NAME = "iMoonUtilJpaRecordRepositoryCacheProperties";

    private final static String[] CACHE_DELIMITERS = {"", ".", "-", ">", ":", "_"};

    private final static Map<String, Object> CACHED_NAMESPACES = new ConcurrentHashMap<>();

    private static <T> T onEnabledRecordCacheOrDefault(
        JpaRecordRepositoryMetadata metadata,
        BiFunction<ApplicationContext, EnableJpaRecordCaching, T> converter,
        T defaultValue
    ) {
        T resultValue = defaultValue;
        try {
            ApplicationContext context = metadata.getApplicationContext();
            if (context.containsBean(CACHE_PROPERTIES_NAME)) {
                EnableJpaRecordCaching cache = context.getBean(CACHE_PROPERTIES_NAME, EnableJpaRecordCaching.class);
                if (context.containsBeanDefinition(cache.cacheManagerRef())) {
                    resultValue = converter.apply(context, cache);
                }
            }
        } catch (Throwable ignored) {
            resultValue = defaultValue;
        }
        return resultValue == null ? defaultValue : resultValue;
    }

    /**
     * 这种实现方式当项目存在同名不同包实体类时，可能引起分布式数据不一致，这个问题可通过如下方式解决
     * <pre>
     * 在实体类上注解{@link RecordCacheable}
     * </pre>
     *
     * @param domainClass
     * @param placeholder
     *
     * @return
     */
    private static String toCacheNamespace(String globalGroup, Class<?> domainClass, Object placeholder) {
        RecordCacheGroup cacheGroup = domainClass.getDeclaredAnnotation(RecordCacheGroup.class);
        String group = cacheGroup == null ? globalGroup : cacheGroup.value(), value;
        RecordCacheable namespaceCfg = domainClass.getDeclaredAnnotation(RecordCacheable.class);
        if (namespaceCfg != null) {
            value = namespaceCfg.name();
            // 如果自定义了 group 和 namespace 直接返回
            if (StringUtil.isNotBlank(value)) {
                String namespace = mergeNamespace(group, value);
                CacheNamespace.putNamespace(domainClass, namespace);
                return namespace;
            }
        }
        // 否则自动推断：实体名、缩写实体名、完全限定名
        String namespace = detectNamespace(group, domainClass.getSimpleName(), domainClass, placeholder);
        if (namespace != null) {
            return namespace;
        }
        for (String delimiter : CACHE_DELIMITERS) {
            String tempName = ClassUtil.getShortName(domainClass, delimiter);
            namespace = detectNamespace(group, tempName, domainClass, placeholder);
            if (namespace != null) {
                return namespace;
            }
        }
        // 完全限定名
        return detectNamespace(group, domainClass.getName(), domainClass, placeholder);
    }

    private static String mergeNamespace(String group, String namespace) {
        return new StringBuilder(group).append(' ').append(namespace).toString().trim().replace(' ', ':');
    }

    /**
     * 检测当前缓存命名空间是否唯一，如果是就缓存并返回，否则直接返回 null
     *
     * @param namespace   命名空间
     * @param placeholder 占位符
     *
     * @return
     */
    private static String detectNamespace(String group, String namespace, Class domainClass, Object placeholder) {
        String groupedNamespace = mergeNamespace(group, namespace);
        if (!CACHED_NAMESPACES.containsKey(groupedNamespace)) {
            CACHED_NAMESPACES.put(groupedNamespace, placeholder);
            CacheNamespace.putNamespace(domainClass, namespace);
            return groupedNamespace;
        }
        return null;
    }

    /**
     * 是否禁用缓存
     *
     * @param domainClass
     *
     * @return
     */
    private static boolean isDisabledCache(Class<?> domainClass) {
        RecordCacheable cacheable = domainClass.getDeclaredAnnotation(RecordCacheable.class);
        return cacheable != null && !cacheable.value();
    }

    final static CacheManager deduceCacheManager(
        JpaRecordRepositoryMetadata metadata, Class<?> domainClass, CacheManager defaultIfAbsent
    ) {
        return isDisabledCache(domainClass) ? defaultIfAbsent : onEnabledRecordCacheOrDefault(metadata,
            (ctx, cache) -> ctx.getBean(cache.cacheManagerRef(), CacheManager.class),
            defaultIfAbsent);
    }

    final static String deduceCacheNamespace(
        JpaRecordRepositoryMetadata metadata, Class<?> domainClass, Object placeholder
    ) {
        return isDisabledCache(domainClass) ? null : onEnabledRecordCacheOrDefault(metadata,
            (ctx, c) -> toCacheNamespace(c.group(), domainClass, placeholder),
            null);
    }
}
