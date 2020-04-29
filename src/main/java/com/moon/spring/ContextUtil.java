package com.moon.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author benshaoye
 */
@Component
public final class ContextUtil implements ApplicationContextAware {

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

    public static <T> T getBean(Class<T> type) { return CTX.getBean(type); }

    public static Object getBean(String beanName) { return CTX.getBean(beanName); }

    public static <T> T getBean(String beanName, Class<T> requiredType) { return CTX.getBean(beanName, requiredType); }

    public static void refresh() {
        if (CTX instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) CTX).refresh();
        } else {
            ConfigurableApplicationContext gblCtx;
            ContextUtil gbl = getBean(ContextUtil.class);
            if ((gblCtx = gbl.context) != null) {
                gblCtx.refresh();
            }
        }
    }
}
