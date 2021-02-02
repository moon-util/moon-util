package com.moon.accessor.meta;

import com.moon.accessor.Conditional;
import com.moon.accessor.MatchingType;
import com.moon.accessor.PropertyGetter;
import com.moon.accessor.PropertySetter;
import com.moon.accessor.dialect.KeywordPolicy;
import com.moon.accessor.dialect.SQLDialect;
import com.moon.accessor.dml.AliasCapable;
import com.moon.accessor.type.TypeHandler;

/**
 * @author benshaoye
 */
public interface TableField<T, R, TB extends Table<R, TB>> extends AliasCapable<TableField<T, R, TB>>, Commentable {

    /**
     * 领域模型类
     *
     * @return 与数据表对应的实体类
     */
    Class<R> getDomainClass();

    /**
     * 返回字段数据类型
     *
     * @return 字段 java 类型
     */
    Class<T> getPropertyType();

    /**
     * 获取属性值
     *
     * @param record 实体
     *
     * @return 实体对应字段的属性
     */
    default T getPropertyValue(R record) { return getPropertyGetter().get(record); }

    /**
     * 获取属性值
     *
     * @param record 实体
     * @param value  指定属性的值
     *
     * @return 实体对应字段的属性
     */
    default void setPropertyValue(R record, T value) { getPropertySetter().set(record, value); }

    /**
     * 是否可获取属性值，即实体类里是否存在相应 getter
     *
     * @return true/false
     */
    default boolean isGetterAvailable() { return getPropertyGetter() != null; }

    /**
     * 是否可设置属性值，即实体类里是否存在相应 setter
     *
     * @return true/false
     */
    default boolean isSetterAvailable() { return getPropertySetter() != null; }

    /**
     * 字段类型处理器
     *
     * @return 字段类型处理器
     */
    TypeHandler<T> getTypeHandler();

    /**
     * 获取属性获取器
     *
     * @return 属性获取器
     */
    PropertyGetter<R, T> getPropertyGetter();

    /**
     * 获取属性设置器
     *
     * @return 属性设置器
     */
    PropertySetter<R, T> getPropertySetter();

    /**
     * 实体字段名
     *
     * @return 实体字段名
     */
    String getPropertyName();

    /**
     * 获取数据库表名
     *
     * @return 数据库表名
     */
    default String getTableName() { return getTable().getTableName(); }

    /**
     * 获取数据库对应的列名
     *
     * @return 列名
     */
    String getColumnName();

    /**
     * 指定方言的数据库列名，不同方言数据库列名可能不同，
     * <p>
     * 如: {@code MySQL}列名会用反引号包裹 {@code `column_name`}，
     * <p>
     * 当没有指定方言时，这里直接返回列名，因为大多数情况下是允许直接使用列名的
     *
     * @return 符合数据库方言规则的列名
     */
    default String getAvailableColumnName() { return getColumnName(); }

    /**
     * 获取符合数据库方言的列名，这主要是为了能在运行时根据自定义数据库方言返回对应信息
     *
     * @param dialect 方言
     *
     * @return 符合数据库方言规则的列名
     */
    default String getAvailableColumnName(SQLDialect dialect) {
        return dialect.asAvailableName(getColumnName(), KeywordPolicy.AUTO);
    }

    /**
     * 获取列别名，当设置了别名是返回别名
     * <p>
     * 没有设置时可根据系统需要分配或返回实际列名{@link #getColumnName()}
     *
     * @return 别名
     *
     * @see #as(String) 指定别名
     */
    default String getAliasName() { return getColumnName(); }

    /**
     * 参考：{@inheritDoc}
     *
     * @return 该字段下的第一行注释
     *
     * @see #getComment() 该字段的所有注释
     */
    @Override
    String getFirstComment();

    /**
     * 参考：{@inheritDoc}
     *
     * @return 该字段的文档注释
     *
     * @see #getFirstComment() 该字段的第一行注释
     */
    @Override
    String getComment();

    /**
     * 返回所属数据表
     *
     * @return 数据表描述
     */
    TB getTable();

    /**
     * 查询时指定别名
     *
     * @param alias 别名
     *
     * @return
     */
    @Override
    TableField<T, R, TB> as(String alias);

    /*
     * 以下是条件查询
     */

    /**
     * 字符串以 xxx 开始，这里用{@link Object}是为了兼容别的数据类型，
     * 内部处理通过{@link String#valueOf(Object)}，最终得到：
     * <pre>
     *     field LIKE 'text%'
     * </pre>
     *
     * @param value 期望字符串
     *
     * @return where 条件
     */
    default Conditional startsWith(Object value) {
        return new Condition(this, MatchingType.starts_with, value);
    }

    /**
     * 字符串以 xxx 开始，这里用{@link Object}是为了兼容别的数据类型，
     * 内部处理通过{@link String#valueOf(Object)}，最终得到：
     * <pre>
     *     field LIKE '%text'
     * </pre>
     *
     * @param value 期望字符串
     *
     * @return where 条件
     */
    default Conditional endsWith(Object value) {
        return null;
    }

    /**
     * 字符串以 xxx 开始，这里用{@link Object}是为了兼容别的数据类型，
     * 内部处理通过{@link String#valueOf(Object)}，最终得到：
     * <pre>
     *     field LIKE '%text%'
     * </pre>
     *
     * @param value 期望字符串
     *
     * @return where 条件
     */
    default Conditional contains(Object value) {
        return null;
    }

    /**
     * 字符串以 xxx 开始，这里用{@link Object}是为了兼容别的数据类型，
     * 内部处理通过{@link String#valueOf(Object)}，最终得到：
     * <pre>
     *     field LIKE 'text'
     * </pre>
     * <p>
     * 注意与{@link #startsWith(Object)}、{@link #endsWith(Object)}、{@link #contains(Object)}
     * 的区别，这里不会主动添加任何匹配符号（如：%、_)等，只是单纯的转换成字符串
     * <p>
     * 这是满足更多自定义条件设计的
     *
     * @param value 期望字符串
     *
     * @return where 条件
     */
    default Conditional like(Object value) {
        return null;
    }

    /**
     * 小于某个值
     *
     * @param value 期望字段最大值（不包含）
     *
     * @return 条件子句
     */
    default Conditional lt(T value) {
        return null;
    }

    /**
     * 大于某个值
     *
     * @param value 期望字段最小值（不包含）
     *
     * @return 条件子句
     */
    default Conditional gt(T value) {
        return null;
    }

    /**
     * 小于某个值
     *
     * @param value 期望字段最大值（包含）
     *
     * @return 条件子句
     */
    default Conditional le(T value) {
        return null;
    }

    /**
     * 大于某个值
     *
     * @param value 期望字段最小值（不包含）
     *
     * @return 条件子句
     */
    default Conditional ge(T value) {
        return null;
    }

    /**
     * 等于某个值
     *
     * @param value 期望的字段值
     *
     * @return 条件子句
     *
     * @see #is(Object) 等价
     */
    default Conditional eq(T value) {
        return null;
    }

    /**
     * 等于某个值
     *
     * @param value 期望的字段值
     *
     * @return 条件子句
     *
     * @see #eq(Object) 等价
     */
    default Conditional is(T value) {
        return null;
    }

    /**
     * 等不于某个值
     *
     * @param value 不期望的字段值
     *
     * @return 条件子句
     */
    default Conditional ne(T value) {
        return null;
    }

    /**
     * 字段是否在指定列表中
     *
     * @param values 期望值列表
     *
     * @return 条件子句
     *
     * @see #in(Iterable) 等价
     * @see #nonOf(Object[])
     */
    default Conditional oneOf(T... values) {
        return null;
    }

    /**
     * 字段是否不在指定列表中
     *
     * @param values 字段值不期望的值的列表
     *
     * @return 条件子句
     *
     * @see #notIn(Iterable) 等价
     * @see #oneOf(Object[])
     */
    default Conditional nonOf(T... values) {
        return null;
    }

    /**
     * 字段是否在指定列表中
     *
     * @param values 期望值列表
     *
     * @return 条件子句
     */
    default Conditional in(Iterable<T> values) {
        return null;
    }

    /**
     * 字段是否不在指定列表中
     *
     * @param values 字段值不期望的值的列表
     *
     * @return 条件子句
     */
    default Conditional notIn(Iterable<T> values) {
        return null;
    }

    /**
     * 期望字段值为 null
     *
     * @return 条件
     */
    default Conditional isNull() {
        return null;
    }

    /**
     * 期望字段值不为 null
     *
     * @return 条件
     */
    default Conditional notNull() {
        return null;
    }

    /**
     * 期望字段值不为 null
     *
     * @return 条件
     */
    default Conditional isNotNull() { return notNull(); }

    /**
     * 与另一个字段相等
     *
     * @param field 另一个字段
     *
     * @return 条件
     */
    default Conditional equalsTo(TableField<?, ?, ? extends Table<?, ?>> field) {
        return null;
    }
}
