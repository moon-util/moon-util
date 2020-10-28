package com.moon.spring.data.jpa.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author moonsky
 */
public class JpaRecordRepositoryMetadata {

    private final ApplicationContext ctx;

    JpaRecordRepositoryMetadata(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    private ApplicationContext ctx() { return ctx; }

    public ApplicationContext getApplicationContext() { return ctx(); }

    public Environment getEnvironment() {
        ApplicationContext ctx = getApplicationContext();
        return ctx == null ? RecordRepositoryNones.Env.ENV : ctx.getEnvironment();
    }

    public boolean containsBean(String beanName) {
        return ctx().containsBeanDefinition(beanName);
    }

    public Object getBean(String beanName) { return ctx().getBean(beanName); }

    public <T> T getBean(Class<T> requiredType) { return ctx().getBean(requiredType); }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return ctx().getBean(beanName, requiredType);
    }

    public String getProperty(String key) { return getEnvironment().getProperty(key); }

    public String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }
}
