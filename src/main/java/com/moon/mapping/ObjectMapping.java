package com.moon.mapping;

import java.util.Objects;

/**
 * 暂未实现
 *
 * @author moonsky
 */
interface ObjectMapping<THIS> {

    /**
     * 按属性值比较两个当前类的值是否全部相等，每个属性值用{@link Objects#equals(Object, Object)}比较是否相等
     *
     * @param thisObject  对象 1
     * @param otherObject 对象 2
     *
     * @return 如果两个对象所有属性都相同返回 true，否则返回 false
     *
     * @see Objects#equals(Object, Object) 各属性的实际比较方式
     */
    boolean isPropertiesEquals(THIS thisObject, THIS otherObject);

    /**
     * 对象按各属性执行{@code toString()}方法
     *
     * @param thisObject 待执行{@code toString}对象
     *
     * @return 对象按属性 toString 后的子串形式
     */
    String toString(THIS thisObject);

    /**
     * 中文和英文在不同终端所占长度不同，暂时难以实现
     * <p>
     * 将对象列表按如下形式返回表格化字符串:
     * <pre>
     * ```------|--------|
     * | field1 | field2 |
     * |--------|--------|
     * | value1 | value2 |
     * |--------|------```
     * </pre>
     *
     * @param thisObject 待表格化字符串对象
     *
     * @return 对象按属性进行表格化后的字符串
     */
    // String toTableString(THIS thisObject);

    /**
     * 中文和英文在不同终端所占长度不同，暂时难以实现
     * <p>
     * 将对象列表按如下形式返回表格化字符串:
     * <pre>
     * ```------|--------|--------|
     * | field1 | field2 | field3 |
     * |--------|--------|--------|
     * | row1v1 | row1v2 | row1v3 |
     * |--------|--------|--------|
     * | row2v1 | row2v2 | row2v3 |
     * |--------|--------|------```
     * </pre>
     *
     * @param thisObject 待表格化字符串对象列表
     *
     * @return 对象按属性进行表格化后的字符串
     */
    // String toTableString(Iterable<THIS> thisObject);
}
