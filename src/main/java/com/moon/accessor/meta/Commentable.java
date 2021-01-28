package com.moon.accessor.meta;

/**
 * 可注释元素
 *
 * @author benshaoye
 */
public interface Commentable {

    /**
     * 返回字段上的所有注释
     * <p>
     * 读取规则如下:
     * <pre>
     * 1. 如果定义了“@comment ”标签就使用该标签下的文档注释内容；
     * 2. 如果没有定义“@comment ”标签，则使用该字段的默认注释内容；
     * 3. 注释内容是当前范围的所有行的内容之和，包括 html 标签，如这里的“@comment ”注释是：“第一行注释，第二行注释”；
     * 4. 如果该字段类型是枚举还会在最后加上所有枚举值以及对应的文档注释；
     * 5. 如果该字段类型是枚举，但文档注释已出现过 1 至多个枚举值，则最后不再追加所有枚举值的文档注释。
     * </pre>
     *
     * @return 该字段的文档注释
     *
     * @comment 第一行注释，
     * 第二行注释
     * @see #getFirstComment() 该字段第一行文档注释
     */
    String getComment();

    /**
     * 返回定义在字段上的第一行注释
     * <p>
     * 具体取用方式参考{@link #getComment()}，不同的是，这里只会读取第一行注释，
     * 包括当字段类型是枚举时，枚举的注释也只取第一行.
     * <p>
     * 备注:
     * <pre>
     * 1. 若 firstComment 与 comment 完全相同，
     *    字段 firstComment 将不保存注释内容，而是返回{@link #getComment()}；
     * 2. 生成的数据库注释使用的是第一行注释，而不是所有文档注释.
     * </pre>
     *
     * @return 该字段下的第一行注释
     *
     * @see #getComment() 该字段所有文档注释
     */
    String getFirstComment();
}
