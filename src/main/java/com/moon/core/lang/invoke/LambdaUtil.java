package com.moon.core.lang.invoke;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.SerializableFunction;

/**
 * @author moonsky
 */
public abstract class LambdaUtil {

    private LambdaUtil() { ThrowUtil.noInstanceError(); }

    public static <T> String getPropertyName(SerializableFunction<T, Object> getter) {
        return toFieldName(SerializedLambda.resolve(getter).getImplMethodName());
    }

    @SuppressWarnings("all")
    private static String toFieldName(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("with")) {
            name = name.substring(4);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set") && !name.startsWith("add")) {
                String msg = "解析属性名错误 '" + name + "', 必须以 'is'、'get'、'set'、'add'、'with' 之一开头";
                throw new IllegalStateException(msg);
            }
            name = name.substring(3);
        }
        return name.length() > 0 ? StringUtil.decapitalize(name) : name;
    }
}
