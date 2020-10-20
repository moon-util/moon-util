package com.moon.spring.data.jpa.start;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author moonsky
 */
@ConditionalOnMissingBean(JpaRecordCacheRegistrar.class)
public class JpaRecordCacheRegistrar implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        return new String[0];
    }
}
