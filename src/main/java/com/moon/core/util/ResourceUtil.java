package com.moon.core.util;

import com.moon.core.util.support.ResourceSupport;

import java.io.InputStream;
import java.util.function.Consumer;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 * @date 2018/9/12
 */
public final class ResourceUtil {
    private ResourceUtil() {
        noInstanceError();
    }

    /**
     * 检测系统资源文件或本地文件是否存在
     *
     * @param sourcePath
     */
    public static final boolean resourceExists(String sourcePath) {
        return ResourceSupport.resourceExists(sourcePath);
    }

    /**
     * 读取系统资源文件或者本地文件的输入流
     *
     * @param sourcePath
     */
    public static final InputStream getResourceAsInputStream(String sourcePath) {
        return ResourceSupport.getResourceAsStream(sourcePath);
    }

    public static final void ifResourceExists(String path, Consumer<InputStream> consumer) {
        InputStream stream = ResourceSupport.getResourceAsStreamOrNull(path);
        if (stream != null) {
            consumer.accept(stream);
        }
    }
}
