package com.moon.core.util.logger;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.util.FilterUtil;
import com.moon.core.util.function.ThrowingFunction;

import java.lang.reflect.Method;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.reflect.MethodUtil.getPublicStaticMethods;

/**
 * @author benshaoye
 */
final class LoggerUtil {

    private LoggerUtil() { noInstanceError(); }

    public final static <T> T slf4j() {
        return null;
    }

    public final static <T> T log4j() {
        return null;
    }

    public final static <T> T log4j2() {
        return null;
    }

    @FunctionalInterface
    private interface LoggerCreator {
        <T> T create(String name);
    }

    private enum Creator implements LoggerCreator {
        SLF4J("org.slf4j.LoggerFactory", "getLogger"),
        LOG4J("", ""),
        LOG4J2("", "");

        final WeakAccessor<ThrowingFunction> ACCESSOR;
        private final String methodName;
        private final String className;

        Creator(String className, String methodName) {
            this.ACCESSOR = WeakAccessor.of(() -> getCreator());
            this.methodName = methodName;
            this.className = className;
        }

        ThrowingFunction getCreator() {
            Class targetClass = ClassUtil.forName(className);
            List<Method> ms = getPublicStaticMethods(targetClass, methodName, String.class);
            Method creator = FilterUtil.requireFirst(ms, m -> m != null);
            return name -> creator.invoke(null, name);
        }

        @Override
        public <T> T create(String name) { return (T) ACCESSOR.get().applyWithUnchecked(name); }
    }
}
