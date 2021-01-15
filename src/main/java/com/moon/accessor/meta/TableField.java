package com.moon.accessor.meta;

import com.moon.accessor.MatchingType;
import com.moon.accessor.PropertyGetter;
import com.moon.accessor.PropertySetter;
import com.moon.accessor.dml.AliasCapable;

import java.sql.PreparedStatement;

/**
 * @author benshaoye
 */
public interface TableField<T, R, TB extends Table<R, TB>> extends AliasCapable<TableField<T, R, TB>> {

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
    default T getPropertyValue(R record) {
        return getPropertyGetter().get(record);
    }

    /**
     * 获取属性值
     *
     * @param record 实体
     *
     * @return 实体对应字段的属性
     */
    default void setPropertyValue(R record, T value) {
        getPropertySetter().set(record, value);
    }

    /**
     * 是否可获取属性值，即实体类里是否存在相应 getter
     *
     * @return true/false
     */
    default boolean isGetterAvailable() {
        return getPropertyGetter() != null;
    }

    /**
     * 是否可设置属性值，即实体类里是否存在相应 setter
     *
     * @return true/false
     */
    default boolean isSetterAvailable() {
        return getPropertySetter() != null;
    }

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
     * 获取指定属性的值
     *
     * @param record 实例数据
     *
     * @return 指定字段的值
     */
    default T getValue(R record) {
        return getPropertyGetter().get(record);
    }

    // void transfer(R record, PreparedStatement statement, int columnIndex);

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
     * 获取列别名，当设置了别名是返回别名
     * <p>
     * 没有设置时可根据系统需要分配或返回实际列名{@link #getColumnName()}
     *
     * @return 别名
     *
     * @see #as(String) 指定别名
     */
    default String getAliasName() {
        return getColumnName();
    }

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
    default Condition startsWith(Object value) {
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
    default Condition endsWith(Object value) {
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
    default Condition contains(Object value) {
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
    default Condition like(Object value) {
        return null;
    }

    /**
     * 小于某个值
     *
     * @param value 期望字段最大值（不包含）
     *
     * @return 条件子句
     */
    default Condition lt(T value) {
        return null;
    }

    /**
     * 大于某个值
     *
     * @param value 期望字段最小值（不包含）
     *
     * @return 条件子句
     */
    default Condition gt(T value) {
        return null;
    }

    /**
     * 小于某个值
     *
     * @param value 期望字段最大值（包含）
     *
     * @return 条件子句
     */
    default Condition le(T value) {
        return null;
    }

    /**
     * 大于某个值
     *
     * @param value 期望字段最小值（不包含）
     *
     * @return 条件子句
     */
    default Condition ge(T value) {
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
    default Condition eq(T value) {
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
    default Condition is(T value) {
        return null;
    }

    /**
     * 等不于某个值
     *
     * @param value 不期望的字段值
     *
     * @return 条件子句
     */
    default Condition ne(T value) {
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
    default Condition oneOf(T... values) {
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
    default Condition nonOf(T... values) {
        return null;
    }

    /**
     * 字段是否在指定列表中
     *
     * @param values 期望值列表
     *
     * @return 条件子句
     */
    default Condition in(Iterable<T> values) {
        return null;
    }

    /**
     * 字段是否不在指定列表中
     *
     * @param values 字段值不期望的值的列表
     *
     * @return 条件子句
     */
    default Condition notIn(Iterable<T> values) {
        return null;
    }

    /**
     * 期望字段值为 null
     *
     * @return 条件
     */
    default Condition isNull() {
        return null;
    }

    /**
     * 期望字段值不为 null
     *
     * @return 条件
     */
    default Condition notNull() {
        return null;
    }

    /**
     * 期望字段值不为 null
     *
     * @return 条件
     */
    default Condition isNotNull() { return notNull(); }

    /**
     * 与另一个字段相等
     *
     * @param field 另一个字段
     *
     * @return 条件
     */
    default Condition equalsTo(TableField<?, ?, ? extends Table<?, ?>> field) {
        return null;
    }
}
