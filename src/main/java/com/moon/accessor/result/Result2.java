package com.moon.accessor.result;

import com.moon.accessor.exception.Exception2;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum Result2 {
    ;

    public static String[] extractColumnsLabel(ResultSet resultSet) {
        try {
            ResultSetMetaData meta = resultSet.getMetaData();
            final int count = meta.getColumnCount();
            String[] labels = new String[count];
            for (int i = 0; i < count; i++) {
                labels[i] = meta.getColumnName(i + 1);
            }
            return labels;
        } catch (SQLException e) {
            throw Exception2.with(e);
        }
    }

    /**
     * 从 SQL 查询结果集中当前行数据并封装成 Map 返回
     *
     * @param resultSet SQL 结果集
     *
     * @return 单行数据对应的 Map
     */
    public static Map<String, Object> map(ResultSet resultSet) {
        try {
            ResultSetMetaData meta = resultSet.getMetaData();
            Map<String, Object> resultMap = new HashMap<>();
            for (int i = 0, c = meta.getColumnCount(), n; i < c; i++) {
                resultMap.put(meta.getColumnLabel(n = i + 1), resultSet.getObject(n));
            }
            return resultMap;
        } catch (SQLException e) {
            throw Exception2.with("提取查询结果错误: ", e);
        }
    }

    /**
     * 从 SQL 查询结果集所有数据封装成 Map 返回
     *
     * @param resultSet SQL 结果集
     *
     * @return 所有结果
     */
    public static List<Map<String, Object>> mapList(ResultSet resultSet) {
        try {
            String[] labels = extractColumnsLabel(resultSet);
            int columnsCount = labels.length;
            List<Map<String, Object>> resultList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> resultMap = new HashMap<>();
                for (int i = 0; i < columnsCount; i++) {
                    resultMap.put(labels[i], resultSet.getObject(i + 1));
                }
                resultList.add(resultMap);
            }
            return resultList;
        } catch (SQLException e) {
            throw Exception2.with("提取查询结果错误: ", e);
        }
    }

    /**
     * 从 SQL 查询结果集中最多提取一行数据
     *
     * @param resultSet SQL 结果集
     * @param mapper    行映射器
     * @param <T>       返回数据类型
     *
     * @return 单行数据或 null
     */
    public static <T> T extractOne(ResultSet resultSet, RowMapper<T> mapper) {
        try {
            if (!resultSet.next()) {
                return null;
            }
            T result = mapper.doMap(resultSet);
            if (resultSet.next()) {
                throw Exception2.with("There is more than one piece of data.");
            }
            return result;
        } catch (SQLException e) {
            throw Exception2.with(e);
        }
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
     */
    public static <T> List<T> extractList(ResultSet resultSet, RowMapper<T> mapper) {
        try {
            List<T> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(mapper.doMap(resultSet));
            }
            return resultList;
        } catch (SQLException e) {
            throw Exception2.with(e);
        }
    }

    /**
     * 自定义完整提取 SQL 查询结果集
     *
     * @param resultSet SQL 查询结果集
     * @param extractor 提取器
     * @param <T>       返回数据类型
     *
     * @return 自定义提取结果
     */
    public static <T> T extract(ResultSet resultSet, ResultExtractor<T> extractor) {
        try {
            return extractor.extract(resultSet);
        } catch (SQLException e) {
            throw Exception2.with(e);
        }
    }
}
