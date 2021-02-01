package com.moon.accessor.session;

import com.moon.accessor.param.ParameterSetter;
import com.moon.accessor.param.Parameters;
import com.moon.accessor.result.ResultExtractor;
import com.moon.accessor.result.RowMapper;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.moon.accessor.result.Result2.atMost1;
import static com.moon.accessor.result.Result2.listAll;

/**
 * @author benshaoye
 */
public interface JdbcSession extends Closeable {

    Object[] EMPTY_PARAMETERS = {};

    /**
     * insert 语句
     *
     * @param sql sql 语句
     *
     * @return 执行结果
     */
    default int insert(String sql) {
        // Example:
        return update(sql);
    }

    /**
     * insert 语句
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 执行结果
     */
    default int insert(String sql, Object[] parameters) {
        // Example:
        return update(sql, parameters);
    }

    /**
     * 用相同结构的参数批量执行 insert 语句
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 每组参数是否插入成功对应的数目
     */
    default int[] insertBatch(String sql, Iterable<Object[]> parameters) {
        // Example:
        return updateBatch(sql, parameters);
    }

    /**
     * update 语句
     *
     * @param sql sql 语句
     *
     * @return 执行结果
     */
    default int update(String sql) {
        // Example:
        return update(sql, EMPTY_PARAMETERS);
    }

    /**
     * update 语句
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 执行结果
     */
    int update(String sql, Object[] parameters);

    /**
     * update 语句
     *
     * <pre>
     * Parameters.of().with(1).with("name")
     * </pre>
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 执行结果
     *
     * @see Parameters
     */
    default int update(String sql, ParameterSetter[] parameters) {
        // Example:
        return update(sql, Arrays.asList(parameters));
    }

    /**
     * update 语句
     *
     * <pre>
     * Parameters.of().with(1).with("name")
     * </pre>
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 执行结果
     *
     * @see Parameters
     */
    int update(String sql, Collection<ParameterSetter> parameters);

    /**
     * 用相同结构的参数批量执行 update 语句
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 执行结果影响的数据数目
     */
    int[] updateBatch(String sql, Iterable<Object[]> parameters);

    /**
     * delete 语句
     *
     * @param sql sql 语句
     *
     * @return 执行结果
     */
    default int delete(String sql) {
        // Example:
        return update(sql);
    }

    /**
     * delete 语句
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 执行结果
     */
    default int delete(String sql, Object[] parameters) {
        // Example:
        return update(sql, parameters);
    }

    /**
     * 用相同结构的参数批量执行 delete 语句
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     *
     * @return 执行结果影响的数据数目
     */
    default int[] deleteBatch(String sql, Iterable<Object[]> parameters) {
        // Example:
        return updateBatch(sql, parameters);
    }

    /**
     * select 语句，最多只返回一行数据，超过一行时抛出异常
     *
     * @param sql    sql 语句
     * @param mapper 行数据映射器
     * @param <T>    返回数据类型
     *
     * @return 最多返回一行数据，或 null
     */
    default <T> T selectOne(String sql, RowMapper<T> mapper) {
        // Example:
        return select(sql, set -> atMost1(set, mapper));
    }

    /**
     * select 语句，返回一个集合，里面包含 0 至多条数据
     * <p>
     * 不返回 null
     *
     * @param sql    sql 语句
     * @param mapper 行数据映射器
     * @param <T>    返回数据类型
     *
     * @return 返回所有数据行集合
     */
    default <T> List<T> selectAll(String sql, RowMapper<T> mapper) {
        // Example:
        return select(sql, set -> listAll(set, mapper));
    }

    /**
     * select 语句，完全控制结果集的映射，并返回数据提取器的返回值
     *
     * @param sql       sql 语句
     * @param extractor 结果集数据提取器
     * @param <T>       返回数据类型
     *
     * @return extractor 原样返回{@code extractor}的返回值
     */
    default <T> T select(String sql, ResultExtractor<T> extractor) {
        // Example:
        return select(sql, EMPTY_PARAMETERS, extractor);
    }

    /**
     * select 语句，最多只返回一行数据，超过一行将抛出异常
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     * @param mapper     行数据映射器
     * @param <T>        返回数据类型
     *
     * @return 最多返回一行数据，或 null
     */
    default <T> T selectOne(String sql, Object[] parameters, RowMapper<T> mapper) {
        // Example:
        return select(sql, parameters, set -> atMost1(set, mapper));
    }

    /**
     * select 语句，返回一个集合，里面包含 0 至多条数据
     * <p>
     * 不返回 null
     *
     * @param sql        sql 预编译语句
     * @param parameters 参数
     * @param mapper     行数据映射器
     * @param <T>        返回数据类型
     *
     * @return 返回所有数据行集合
     */
    default <T> List<T> selectAll(String sql, Object[] parameters, RowMapper<T> mapper) {
        // Example:
        return select(sql, parameters, set -> listAll(set, mapper));
    }

    /**
     * select 语句，完全控制结果集的映射，并返回数据提取器的返回值
     *
     * @param sql        sql 语句
     * @param parameters 参数
     * @param extractor  结果集数据提取器
     * @param <T>        返回数据类型
     *
     * @return extractor 原样返回{@code extractor}的返回值
     */
    <T> T select(String sql, Object[] parameters, ResultExtractor<T> extractor);
}
