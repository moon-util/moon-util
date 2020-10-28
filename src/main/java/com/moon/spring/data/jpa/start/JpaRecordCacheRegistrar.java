package com.moon.spring.data.jpa.start;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author moonsky
 */
@ConditionalOnMissingBean(JpaRecordCacheRegistrar.class)
public class JpaRecordCacheRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * @see EnableJpaRecordCaching#cacheManagerRef()
     */
    private final static String PROPERTY_FOR_CACHE_MANAGER_REF = "cacheManagerRef";
    /**
     * @see EnableJpaRecordCaching#group()
     */
    private final static String PROPERTY_FOR_GROUP = "group";
    /**
     * @see com.moon.spring.data.jpa.factory.RecordCacheUtil#CACHE_PROPERTIES_NAME
     */
    @SuppressWarnings("all")
    private final static String CACHE_PROPERTIES_NAME = "iMoonUtilJpaRecordRepositoryCacheProperties";

    @Override
    public void registerBeanDefinitions(
        AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator
    ) {
        // 只有一个有效注册 EnableJpaRecordCache，其余均忽略
        if (registry.containsBeanDefinition(CACHE_PROPERTIES_NAME)) {
            return;
        }
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableJpaRecordCaching.class.getName());
        Object cacheManagerBeanName = attributes.get(PROPERTY_FOR_CACHE_MANAGER_REF);
        Object cacheGroupValue = attributes.get(PROPERTY_FOR_GROUP);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(EnableJpaRecordCaching.class,
            () -> new JpaRecordCacheProperties(cacheManagerBeanName, cacheGroupValue));
        registry.registerBeanDefinition(CACHE_PROPERTIES_NAME, builder.getBeanDefinition());
    }

    private static String emptyIfNull(Object value) { return value == null ? "" : value.toString(); }

    private static class JpaRecordCacheProperties implements EnableJpaRecordCaching {

        private final String cacheManagerRef;
        private final String group;

        public JpaRecordCacheProperties(Object cacheManagerRef, Object group) {
            this.cacheManagerRef = emptyIfNull(cacheManagerRef);
            this.group = emptyIfNull(group);
        }

        @Override
        public Class<? extends Annotation> annotationType() { return EnableJpaRecordCaching.class; }

        @Override
        public String cacheManagerRef() { return cacheManagerRef; }

        @Override
        public String group() { return group; }
    }
}
