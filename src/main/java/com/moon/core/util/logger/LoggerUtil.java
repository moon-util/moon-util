package com.moon.core.util.logger;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.EnumUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.util.FilterUtil;
import com.moon.core.util.function.ThrowingFunction;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.reflect.MethodUtil.getPublicStaticMethods;

/**
 * @author benshaoye
 */
final class LoggerUtil {

    private LoggerUtil() { noInstanceError(); }

    public final static <T> T getSlf4j() { return Creator.SLF4J.create(caller()); }

    public final static <T> T getLog4j() { return Creator.LOG4J.create(caller()); }

    public final static <T> T getLog4j2() { return Creator.LOG4J2.create(caller()); }

    public final static <T> T getDefault() { return Creator.DEFAULT.create(caller()); }

    public final static void setDefaultOnlyOnce(LoggerType type) {
    }

    public enum LoggerType {
        SLF4J, LOG4J, LOG4J2;

        Creator getCreator() { return Creator.valueOf(name()); }
    }

    private static final String caller() { return null; }

    private enum Creator {
        SLF4J("org.slf4j.LoggerFactory", "getLogger"),
        LOG4J("org.apache.log4j.Logger", "getLogger"),
        LOG4J2("org.apache.logging.log4j.LogManager", "getLogger"),
        DEFAULT(null, null) {
            private Creator creator;
            private String settingMessage;

            synchronized Creator setCreator(Creator creator, String settingMessage) {
                if (this.settingMessage == null) {
                    this.creator = Objects.requireNonNull(creator);
                    this.settingMessage = settingMessage;
                } else if (settingMessage != null) {
                    ThrowUtil.doThrow(this.settingMessage);
                }
                return creator;
            }

            @Override
            ThrowingFunction getCreator() {
                if (creator != null) {
                    return creator.getCreator();
                } else {
                    Class<Creator> type = (Class<Creator>) getClass();
                    Creator[] creators = EnumUtil.values(type);
                    for (Creator constant : creators) {
                        if (constant != this) {
                            try {
                                constant.create(type);
                                return setCreator(constant, null).getCreator();
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

        public <T> T create(Class clazz) { return create(clazz.getName()); }

        public <T> T create(String name) { return (T) accessor.get().orWithUnchecked(name); }
    }
}
