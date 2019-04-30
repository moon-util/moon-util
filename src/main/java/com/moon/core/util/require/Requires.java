package com.moon.core.util.require;

/**
 * 不推荐使用，测试有很多现成的框架，而且以后这个模块（assertions）很可能移除
 *
 * @author benshaoye
 */
public interface Requires extends
    RequiresString, RequiresObject, RequiresBoolean,
    RequiresThrows, RequiresMath, RequiresType,
    RequiresEmpty {

    /**
     * 默认：
     * 通过 —— 打印信息
     * 失败 —— 抛出异常，终止程序
     *
     * @return
     */
    static Requires of() {
        return new GenericRequires();
    }

    /**
     * 无任何信息输出，失败抛出异常并退出程序
     *
     * @return
     */
    static Requires ofThrow() {
        Requires requires = of().setAllowConsole(false);
        requires.getConsole().setAllowOutputStatus(false);
        return requires;
    }

    /**
     * 只输出信息，不抛出异常
     *
     * @return
     */
    static Requires ofPrintln() {
        return of().setAllowThrow(false);
    }

    /**
     * 只输出错误信息，不抛出异常，不打印成功信息
     *
     * @return
     */
    static Requires ofError() {
        return ofThrow().setAllowThrow(false);
    }

    /**
     * 不输出任何信息，不抛出任何异常，只返回是否符合条件
     *
     * @return
     */
    static Requires asserts() {
        Requires requires = ofError();
        requires.getConsole().setAllowOutputStatus(false);
        return requires;
    }

    /**
     * 设置是否允许输出
     *
     * @param allowConsole
     * @return
     */
    @Override
    Requires setAllowConsole(boolean allowConsole);

    /**
     * 设置在测试失败时是否抛出异常
     *
     * @param allowThrow
     * @return
     */
    @Override
    Requires setAllowThrow(boolean allowThrow);
}
