package com.moon.spring.data.jpa.factory;

import com.moon.core.enums.Arrays2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
final class RecordRepositoryNones {

    private final static String[] STRINGS = Arrays2.STRINGS.empty();

    enum Ctx implements ApplicationContext {
        CTX;

        @Override
        public String getId() { return name(); }

        @Override
        public String getApplicationName() { return name(); }

        @Override
        public String getDisplayName() { return name(); }

        @Override
        public long getStartupDate() { return System.currentTimeMillis(); }

        @Override
        public ApplicationContext getParent() { return null; }

        @Override
        public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public BeanFactory getParentBeanFactory() { return null; }

        @Override
        public boolean containsLocalBean(String name) { return false; }

        @Override
        public boolean containsBeanDefinition(String beanName) { return false; }

        @Override
        public int getBeanDefinitionCount() { return 0; }

        @Override
        public String[] getBeanDefinitionNames() { return STRINGS; }

        @Override
        public String[] getBeanNamesForType(ResolvableType type) { return STRINGS; }

        @Override
        public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
            return STRINGS;
        }

        @Override
        public String[] getBeanNamesForType(Class<?> type) { return STRINGS; }

        @Override
        public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
            return STRINGS;
        }

        @Override
        public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
            return new HashMap<>(0);
        }

        @Override
        public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException { return new HashMap<>(0); }

        @Override
        public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
            return STRINGS;
        }

        @Override
        public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
            throws BeansException { return new HashMap<>(0); }

        @Override
        public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
            throws NoSuchBeanDefinitionException { return null; }

        @Override
        public Object getBean(String name) throws BeansException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public Object getBean(String name, Object... args) throws BeansException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public <T> T getBean(Class<T> requiredType) throws BeansException {
            throw new NoSuchBeanDefinitionException(requiredType);
        }

        @Override
        public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
            throw new NoSuchBeanDefinitionException(requiredType);
        }

        @Override
        public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
            return new Provider(requiredType);
        }

        @Override
        public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
            return new Provider(requiredType.getRawClass());
        }

        @Override
        public boolean containsBean(String name) { return false; }

        @Override
        public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
            throw new NoSuchBeanDefinitionException(name);
        }

        @Override
        public String[] getAliases(String name) { return STRINGS; }

        @Override
        public void publishEvent(Object event) { }

        @Override
        public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
            return null;
        }

        @Override
        public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
            return null;
        }

        @Override
        public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
            return null;
        }

        @Override
        public Environment getEnvironment() { return Env.ENV; }

        @Override
        public Resource[] getResources(String locationPattern) throws IOException {
            return new Resource[0];
        }

        @Override
        public Resource getResource(String location) { return null; }

        @Override
        public ClassLoader getClassLoader() { return getClass().getClassLoader(); }
    }

    static class Provider implements ObjectProvider {

        private final Class requiredType;

        Provider(Class requiredType) {this.requiredType = requiredType;}

        @Override
        public Object getObject(Object... args) throws BeansException {
            throw new NoSuchBeanDefinitionException(requiredType);
        }

        @Override
        public Object getIfAvailable() throws BeansException { return null; }

        @Override
        public Object getIfUnique() throws BeansException { return null; }

        @Override
        public Object getObject() throws BeansException {
            throw new NoSuchBeanDefinitionException(requiredType);
        }
    }

    @SuppressWarnings("all")
    enum Env implements Environment {
        /**
         * 单例
         */
        ENV;

        static final String[] EMPTY = new String[0];

        @Override
        public String[] getActiveProfiles() { return EMPTY; }

        @Override
        public String[] getDefaultProfiles() { return EMPTY; }

        @Override
        public boolean acceptsProfiles(String... profiles) { return false; }

        @Override
        public boolean acceptsProfiles(Profiles profiles) { return false; }

        @Override
        public boolean containsProperty(String key) { return false; }

        @Override
        public String getProperty(String key) { return null; }

        @Override
        public String getProperty(String key, String defaultValue) { return defaultValue; }

        @Override
        public <T> T getProperty(String key, Class<T> targetType) { return null; }

        @Override
        public <T> T getProperty(String key, Class<T> targetType, T v) { return v; }

        @Override
        public String getRequiredProperty(String key) { throw new IllegalStateException(key); }

        @Override
        public <T> T getRequiredProperty(String key, Class<T> targetType) { throw new IllegalStateException(key); }

        @Override
        public String resolvePlaceholders(String text) { return null; }

        @Override
        public String resolveRequiredPlaceholders(String text) { throw new IllegalStateException(text); }
    }
}
