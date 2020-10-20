package com.moon.spring.data.jpa.start;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * @author moonsky
 */
@ConditionalOnMissingBean(JpaRecordRepositoriesRegistrar.class)
public class JpaRecordRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() { return EnableJpaRecordRepositories.class; }

    @Override
    protected RepositoryConfigurationExtension getExtension() { return new JpaRecordRepositoryConfigExtension(); }

    private static class JpaRecordRepositoryConfigExtension extends JpaRepositoryConfigExtension {

        @Override
        public String getRepositoryFactoryBeanClassName() {
            return JpaRecordRepositoryFactoryBean.class.getName();
        }
    }
}
