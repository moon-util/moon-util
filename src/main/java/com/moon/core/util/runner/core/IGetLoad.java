package com.moon.core.util.runner.core;

import com.moon.core.awt.ImageUtil;
import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.enums.Chars;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.annotation.AnnotationUtil;
import com.moon.core.lang.ref.ReferenceUtil;
import com.moon.core.lang.reflect.FieldUtil;
import com.moon.core.lang.reflect.ModifierUtil;
import com.moon.core.mail.EmailUtil;
import com.moon.core.math.BigDecimalUtil;
import com.moon.core.net.HttpUtil;
import com.moon.core.script.ScriptUtil;
import com.moon.core.security.EncryptUtil;
import com.moon.core.sql.ResultSetUtil;
import com.moon.core.time.TimeUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.concurrent.ExecutorUtil;
import com.moon.core.util.env.EnvUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
class IGetLoad {
    private IGetLoad() { noInstanceError(); }

    private final static String[] packages;

    static {
        /**
         * 每个包下列举一个类，得到包名，支持这个包下的所有类静态方法调用
         */
        Class[] classes = {
            System.class, Method.class, List.class,
            ImageUtil.class, BeanInfoUtil.class, Chars.class,
            IOUtil.class, StringUtil.class, ListUtil.class,
            ReferenceUtil.class, FieldUtil.class, AnnotationUtil.class,
            EmailUtil.class, BigDecimalUtil.class, HttpUtil.class,
            TimeUtil.class, ScriptUtil.class, EncryptUtil.class,
            ResultSetUtil.class, ExecutorUtil.class, EnvUtil.class,
        };
        String[] names = packages = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            names[i] = classes[i].getPackage().getName() + ".";
        }
    }

    private final static Map<String, Class> CACHE = ReferenceUtil.weakMap();

    public final static Class tryLoad(String name) {
        Class type = CACHE.get(name);
        if (type == null) {
            for (String packageName : packages) {
                try {
                    type = ClassUtil.forName(packageName + name);
                    if (ModifierUtil.isPublic(type)) {
                        synchronized (CACHE) {
                            CACHE.put(name, type);
                        }
                        return type;
                    }
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
        return type;
    }

    public final static Class of(String name) {
        Class type = tryLoad(name);
        if (type == null) {
            throw new IllegalArgumentException("can not find class of key: " + name);
        }
        return type;
    }
}
