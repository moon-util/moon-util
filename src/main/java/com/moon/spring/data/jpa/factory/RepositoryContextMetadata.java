package com.moon.spring.data.jpa.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author moonsky
 */
public class RepositoryContextMetadata {

    private final ApplicationContext applicationContext;
    private final AnnotationMetadata annotationMetadata;

    RepositoryContextMetadata(
        ApplicationContext applicationContext, AnnotationMetadata annotationMetadata
    ) {
        this.applicationContext = applicationContext;
        this.annotationMetadata = annotationMetadata;
    }
}
