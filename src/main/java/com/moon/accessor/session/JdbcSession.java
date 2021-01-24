package com.moon.accessor.session;

import com.moon.accessor.result.ResultExtractor;
import com.moon.accessor.result.RowMapper;

import java.io.Closeable;
import java.util.List;

import static com.moon.accessor.result.Result2.extractList;
import static com.moon.accessor.result.Result2.extractOne;

/**
 * @author benshaoye
 */
public interface JdbcSession extends Closeable {

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
     * update 语句
     *
     * @param sql sql 语句
     *
     * @return 执行结果
     */
    int update(String sql);

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
        return select(sql, set -> extractOne(set, mapper));
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
        return select(sql, set -> extractList(set, mapper));
    }

    /**
     * select 语句，完全控制结果集的映射，并返回数据提取器的返回值
     *
     * @param sql       sql 语句
     * @param extractor 结果集数据提取器
     * @param <T>       返回数据类型
     *
     * @return 返回结果集数据提取器的返回值
     */
    <T> T select(String sql, ResultExtractor<T> extractor);

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
        return select(sql, parameters, set -> extractOne(set, mapper));
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
        return select(sql, parameters, set -> extractList(set, mapper));
    }

    /**
     * select 语句，完全控制结果集的映射，并返回数据提取器的返回值
     *
     * @param sql        sql 语句
     * @param parameters 参数
     * @param extractor  结果集数据提取器
     * @param <T>        返回数据类型
     *
     * @return 返回结果集数据提取器的返回值
     */
    <T> T select(String sql, Object[] parameters, ResultExtractor<T> extractor);
}
