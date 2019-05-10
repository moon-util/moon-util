package com.moon.core.util.runner;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Runner {

    /**
     * 如果表达式是含有参数的，用指定参数运行
     * <p>
     * 如果表达式中含有 data 中不包含的变量名，将抛出异常
     *
     * @param data
     * @param <T>
     * @return
     */
    <T> T run(Object data);

    /**
     * 如果表达式不包含参数，运行无参表达式
     *
     * @param <T>
     * @return
     */
    default <T> T run() { return run(null); }

    /**
     * 如果需要使用多个参数，用指定的多参数列表运行解析的表达式
     * <p>
     * 参数列表优先级——相同键名，后面的值将覆盖前面的值——用一个例子说明：
     * <p>
     * 如果参数列表是两个都包含 name 键的 Map，得到的是后面 Map 的值
     * <p>
     * Map map1 = new HashMap();
     * <p>
     * map1.put("name", 1);
     * <p>
     * Map map2 = new HashMap();
     * <p>
     * map2.put("name", 2);
     * <p>
     * Runner runner = RunnerUtil.parse("name");
     * <p>
     * runner.runMulti(map1, map2); // 2
     *
     * @param data
     * @param <T>
     * @return
     */
    default <T> T runMulti(Object... data) { return run(new RunnerDataMap(data)); }
}
