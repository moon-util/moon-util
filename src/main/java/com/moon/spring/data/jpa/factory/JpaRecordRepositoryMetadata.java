package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.JpaRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;

import java.io.Serializable;

/**
 * @author moonsky
 */
public class JpaRecordRepositoryMetadata<T extends JpaRecord<ID>, ID extends Serializable> {

    private final Class<? extends T> repositoryInterface;
    private final ApplicationContext ctx;
    private final EntityInformation<T, ID> ei;
    private final RepositoryInformation ri;

    JpaRecordRepositoryMetadata(
        ApplicationContext ctx,
        RepositoryInformation ri,
        EntityInformation<T, ID> ei,
        Class<? extends T> repositoryInterface
    ) {
        this.ei = ei;
        this.ri = ri;
        this.ctx = ctx;
        this.repositoryInterface = repositoryInterface;
    }

    private ApplicationContext ctx() { return ctx; }

    public RepositoryInformation getRepositoryInformation() { return ri; }

    public Class<? extends T> getRepositoryInterface() { return repositoryInterface; }

    public EntityInformation<T, ID> getEntityInformation() { return ei; }

    public ApplicationContext getApplicationContext() { return ctx(); }

    public Environment getEnvironment() {
        ApplicationContext ctx = getApplicationContext();
        return ctx == null ? RecordRepositoryNones.Env.ENV : ctx.getEnvironment();
    }

    public boolean containsBean(String beanName) {
        return ctx().containsBeanDefinition(beanName);
    }

    public Object getBean(String beanName) { return ctx().getBean(beanName); }

    public <E> E getBean(Class<E> requiredType) { return ctx().getBean(requiredType); }

    public <E> E getBean(String beanName, Class<E> requiredType) {
        return ctx().getBean(beanName, requiredType);
    }

    public String getProperty(String key) { return getEnvironment().getProperty(key); }

    public String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }
}
