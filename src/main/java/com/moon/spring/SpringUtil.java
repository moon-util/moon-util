package com.moon.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author moonsky
 */
public final class SpringUtil implements ApplicationContextAware {

    private static volatile ApplicationContext CTX;

    @Autowired(required = false)
    private ConfigurableApplicationContext context;

    @Override
    public synchronized void setApplicationContext(ApplicationContext ctx) {
        if (context == null && ctx instanceof ConfigurableApplicationContext) {
            context = (ConfigurableApplicationContext) ctx;
        }
        CTX = ctx;
    }

    public static ApplicationContext getContext() { return CTX; }

    public static <T> T getBean(Class<T> type) {
        return CTX == null ? null : CTX.getBean(type);
    }

    public static Object getBean(String beanName) {
        return CTX == null ? null : CTX.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType) {
        return CTX == null ? null : CTX.getBean(beanName, requiredType);
    }

    public static Environment getEnvironment() {
        ApplicationContext ctx = getContext();
        return ctx == null ? null : ctx.getEnvironment();
    }

    public static String getProperty(String key) { return getProperty(key, null); }

    public static String getProperty(String key, String defaultValue) {
        Environment env = getEnvironment();
        return env == null ? defaultValue : env.getProperty(key, defaultValue);
    }

    public static void publish(ApplicationEvent event) { publish((Object) event); }

    public static void publish(Object event) {
        ApplicationContext ctx = getContext();
        if (ctx != null) {
            ctx.publishEvent(event);
        }
    }

    public static void refresh() {
        if (CTX instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) CTX).refresh();
        } else {
            ConfigurableApplicationContext gblCtx;
            SpringUtil gbl = getBean(SpringUtil.class);
            if ((gblCtx = gbl.context) != null) {
                gblCtx.refresh();
            }
        }
    }
}
