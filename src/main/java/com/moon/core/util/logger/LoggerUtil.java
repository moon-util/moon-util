package com.moon.core.util.logger;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.EnumUtil;
import com.moon.core.lang.StackTraceUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.util.FilterUtil;
import com.moon.core.util.function.ThrowingFunction;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.moon.core.lang.StackTraceUtil.getPrevTraceOfSteps;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.reflect.MethodUtil.getPublicStaticMethods;

/**
 * @author benshaoye
 */
public final class LoggerUtil {

    private LoggerUtil() { noInstanceError(); }

    public final static <T> T get() { return Creator.DEFAULT.get(caller()); }

    public final static <T> T slf4j() { return Creator.SLF4J.get(caller()); }

    public final static <T> T log4j() { return Creator.LOG4J.get(caller()); }

    public final static <T> T log4j2() { return Creator.LOG4J2.get(caller()); }

    public final static void setDefaultOnlyOnce(LoggerType type) { Default.setCreator(type); }

    private static final String caller() { return getPrevTraceOfSteps(1).getClassName(); }

    public enum LoggerType {
        SLF4J, LOG4J, LOG4J2;

        Creator getCreator() { return Creator.valueOf(name()); }
    }

    private final static class Default {

        private volatile static Creator creator;
        private volatile static String settingMessage;

        private final static synchronized Creator set(Creator c) {
            return creator = Objects.requireNonNull(c);
        }

        private final static synchronized Creator setCreator(LoggerType c) {
            if (creator == null || settingMessage == null) {
                String trace = StackTraceUtil.toString(getPrevTraceOfSteps(2));
                trace = String.format("Created by: %s", trace);
                creator = c.getCreator();
                settingMessage = trace;
            } else {
                ThrowUtil.doThrow(settingMessage);
            }
            return creator;
        }
    }

    private enum Creator {
        SLF4J("org.slf4j.LoggerFactory", "getLogger"),
        LOG4J("org.apache.log4j.Logger", "getLogger"),
        LOG4J2("org.apache.logging.log4j.LogManager", "getLogger"),
        DEFAULT(null, null) {
            @Override
            ThrowingFunction getCreator() {
                if (Default.creator != null) {
                    return Default.creator.getCreator();
                } else {
                    Class<Creator> type = Creator.class;
                    Creator[] creators = EnumUtil.values(type);
                    for (Creator constant : creators) {
                        if (constant != this) {
                            try {
                                constant.get(type);
                                return Default.set(constant).getCreator();
                            } catch (Throwable t) {
                                // ignore
                            }
                        }
                    }
                }
                return ThrowUtil.doThrow();
            }
        };

        private final WeakAccessor<ThrowingFunction> accessor;
        private final String methodName;
        private final String className;

        Creator(String className, String methodName) {
            this.accessor = WeakAccessor.of(() -> getCreator());
            this.methodName = methodName;
            this.className = className;
        }

        ThrowingFunction getCreator() {
            Class targetClass = ClassUtil.forName(className);
            List<Method> ms = getPublicStaticMethods(targetClass, methodName, String.class);
            Method creator = FilterUtil.requireFirst(ms, m -> m != null);
            return name -> creator.invoke(null, name);
        }

        public <T> T get(Class clazz) { return get(clazz.getName()); }

        public <T> T get(String name) {
            ThrowingFunction getter = accessor.get();
            return (T) getter.orWithUnchecked(name);
        }
    }
}
