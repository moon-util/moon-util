package com.moon.core.enums;

import com.moon.core.util.PropertiesUtil;
import com.moon.core.util.ResourceUtil;
import com.moon.core.util.logger.LoggerUtil;

import static com.moon.core.lang.StringUtil.isNotBlank;
import static com.moon.core.util.PropertiesUtil.getString;

/**
 * @author benshaoye
 */
public enum MoonProps implements PropsSupplier {
    /**
     * 系统配置文件
     */
    moon_configure {
        @Override
        public String value() { return This.propsPath; }
    },
    /**
     * console 输出默认路径
     *
     * @see com.moon.core.util.Console
     */
    moon_console_base_path,
    /**
     * 是否是生产环境
     * <p>
     * com.moon.core.util.env.EnvEnum
     *
     * @see com.moon.core.util.env.EnvUtil
     * @see com.moon.core.util.env.Environmental
     */
    moon_env_development,
    /**
     * 默认日志类型
     *
     * @see LoggerUtil#get()
     */
    moon_logger_type,
    ;

    /**
     * 如果系统参数定义了 moon.configure 参数，且参数值为 properties 文件，
     * 则参数值就是配置文件地址
     * <p>
     * 如果系统参数定义了 moon.configure 参数，但参数值不是 properties 文件，
     * 则默认配置文件为 {@link #MOON_CONFIGURE_PATH} moon-configure.properties
     * <p>
     * 否则，系统检测是否存在 {@link #MOON_CONFIGURE_PATH}
     * moon-configure.properties 文件，存在则为配置文件
     * <p>
     * 其他情况都默认采用系统参数设置{@link System#getProperty(String)}
     */
    private final static class This {
        final static boolean isProperties;
        final static String propsPath;

        static {
            String suffix = ".properties";
            String path = MOON_CONFIGURE_PATH;
            String cfgVal = systemProp(moon_configure.key());
            if (isNotBlank(cfgVal)) {
                isProperties = true;
                propsPath = cfgVal.endsWith(suffix) ? cfgVal : cfgVal + suffix;
            } else {
                isProperties = ResourceUtil.resourceExists(path)
                    && !PropertiesUtil.get(path).isEmpty();
                propsPath = isProperties ? path : null;
            }
        }
    }

    public final static String MOON_CONFIGURE_PATH = "moon-configure.properties";

    static String systemProp(String key) { return System.getProperty(key); }

    private final String key;

    MoonProps() { key = name().replace('_', '.'); }

    @Override
    public final String key() { return key; }

    @Override
    public String value() { return This.isProperties ? getString(This.propsPath, key) : systemProp(key); }

    @Override
    public final String getName() { return key(); }

    @Override
    public final String getText() { return name(); }
}
