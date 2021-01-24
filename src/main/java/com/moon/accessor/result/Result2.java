package com.moon.accessor.result;

import com.moon.accessor.exception.Exception2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public enum Result2 {
    ;

    /**
     * 从 SQL 查询结果集中最多提取一行数据
     *
     * @param resultSet SQL 结果集
     * @param mapper    行映射器
     * @param <T>       返回数据类型
     *
     * @return 单行数据或 null
     *
     * @throws SQLException SQLException
     */
    public static <T> T extractOne(ResultSet resultSet, RowMapper<T> mapper) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        T result = mapper.doMap(resultSet);
        if (resultSet.next()) {
            throw Exception2.with("There is more than one piece of data.");
        }
        return result;
    }

    /**
     * 从 SQL 查询结果集中提取所有行数据
     * <p>
     * 至少返回空集合，不返回 null
     *
     * @param resultSet SQL 查询结果集
     * @param mapper    行映射器
     * @param <T>       行数据对应的数据类型
     *
     * @return 结果集所对应的所有行数据
     *
     * @throws SQLException SQLException
     */
    public static <T> List<T> extractList(ResultSet resultSet, RowMapper<T> mapper) throws SQLException {
        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            resultList.add(mapper.doMap(resultSet));
        }
        return resultList;
    }

    /**
     * 自定义完整提取 SQL 查询结果集
     *
     * @param resultSet SQL 查询结果集
     * @param extractor 提取器
     * @param <T>       返回数据类型
     *
     * @return 自定义提取结果
     *
     * @throws SQLException SQLException
     */
    public static <T> T extract(ResultSet resultSet, ResultExtractor<T> extractor) throws SQLException {
        return extractor.extract(resultSet);
    }
}
