package com.moon.spring.data.jpa.start;

import com.moon.spring.boot.MoonUtilConfiguration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.lang.annotation.*;

/**
 * @author moonsky
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({JpaRecordRepositoriesRegistrar.class, MoonUtilConfiguration.class})
public @interface EnableJpaRecordRepositories {

    /**
     * @see EnableJpaRepositories#value()
     */
    String[] value() default {};

    /**
     * @see EnableJpaRepositories#basePackages()
     */
    String[] basePackages() default {};

    /**
     * @see EnableJpaRepositories#basePackageClasses()
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 从 basePackages 中过滤出符合规则类
     */
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * 从 basePackages 中剔除符合过滤规则类
     */
    ComponentScan.Filter[] excludeFilters() default {};

    /**
     * repository 实现后缀
     */
    String repositoryImplementationPostfix() default "Impl";

    /**
     * Configures the location of where to find the Spring Data named queries properties file. Will default to
     * {@code META-INF/jpa-named-queries.properties}.
     *
     * @return
     */
    String namedQueriesLocation() default "";

    /**
     * Returns the key of the {@link QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link QueryLookupStrategy.Key#CREATE_IF_NOT_FOUND}.
     *
     * @return
     */
    QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link JpaRecordRepositoryFactoryBean}.
     *
     * @return
     */
    Class<?> repositoryFactoryBeanClass() default JpaRecordRepositoryFactoryBean.class;

    /**
     * @see EnableJpaRepositories#repositoryBaseClass()
     */
    Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    // JPA specific configuration

    /**
     * Configures the name of the {@link EntityManagerFactory} bean definition to be used to create repositories
     * discovered through this annotation. Defaults to {@code entityManagerFactory}.
     *
     * @return
     */
    String entityManagerFactoryRef() default "entityManagerFactory";

    /**
     * Configures the name of the {@link PlatformTransactionManager} bean definition to be used to create repositories
     * discovered through this annotation. Defaults to {@code transactionManager}.
     *
     * @return
     */
    String transactionManagerRef() default "transactionManager";

    /**
     * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
     * repositories infrastructure.
     */
    boolean considerNestedRepositories() default false;


    /**
     * @see EnableJpaRepositories#enableDefaultTransactions()
     */
    boolean enableDefaultTransactions() default true;

    /**
     * @see EnableJpaRepositories#bootstrapMode()
     */
    BootstrapMode bootstrapMode() default BootstrapMode.DEFAULT;

    /**
     * Configures what character is used to escape the wildcards {@literal _} and {@literal %} in derived queries with
     * {@literal contains}, {@literal startsWith} or {@literal endsWith} clauses.
     *
     * @return a single character used for escaping.
     */
    char escapeCharacter() default '\\';
}
