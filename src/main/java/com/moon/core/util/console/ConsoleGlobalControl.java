package com.moon.core.util.console;

import com.moon.core.enums.MoonProps;
import com.moon.core.util.Console;
import com.moon.core.util.Console.Level;

import java.io.File;

/**
 * 全局输出控制
 *
 * @author benshaoye
 */
enum ConsoleGlobalControl implements ConsoleEnabled {

    GLOBAL;

    private ConsoleSettings settings = ConsoleDefault.SETTINGS;
    /**
     * 全局是否允许输出控制台
     */
    private boolean allowSystemOut = true;
    /**
     * 全局最低输出级别
     */
    private Level GLOBAL_LEVEL;
    /**
     * 全局输出文件路径
     */
    private File basePath;

    ConsoleGlobalControl() {
        // 留着初始化 base path
        setBasePath(MoonProps.moon_console_base_path.getOrNull());
        GLOBAL_LEVEL = Console.LOWEST;
    }

    @Override
    public synchronized ConsoleGlobalControl setAllowSystemOut(boolean allowSystemOut) {
        this.allowSystemOut = allowSystemOut;
        return this;
    }

    @Override
    public boolean isAllowSystemOut() {
        return allowSystemOut;
    }

    /**
     * 设置最低输出级别
     *
     * @param lowestLevel
     */
    @Override
    public synchronized boolean setLowestLevel(Level lowestLevel) {
        GLOBAL_LEVEL = lowestLevel;
        return true;
    }

    /**
     * 返回最多输出级别
     *
     * @return 最低输出级别
     */
    @Override
    public Level getLowestLevel() {
        return GLOBAL_LEVEL;
    }

    @Override
    public boolean isEnabled(Level level) {
        return level.ordinal() <= GLOBAL_LEVEL.ordinal();
    }

    public File getBasePath() {
        return basePath;
    }

    private void setBasePath(String basePath) {
        if (basePath != null) {
            setBasePath(new File(basePath));
        }
    }

    private void setBasePath(File basePath) {
        if (basePath != null) {
            if (basePath.exists() || basePath.mkdirs()) {
                File dir = new File(basePath, name());
                basePath = dir.mkdirs() ? dir : basePath;
                synchronized (this) {
                    if (this.basePath == null) {
                        this.basePath = basePath;
                    }
                }
            } else {
                System.err.println("[ERROR] Not a valid path: " + basePath);
            }
        }
    }

    public ConsoleSettings getSettings() {
        return settings == null ? ConsoleDefault.SETTINGS : settings;
    }

    public synchronized void setSettings(ConsoleSettings settings) {
        this.settings = settings;
    }
}
