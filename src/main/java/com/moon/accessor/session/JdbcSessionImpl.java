package com.moon.accessor.session;

import com.moon.accessor.exception.Exception2;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author benshaoye
 */
public class JdbcSessionImpl implements JdbcSession {

    private final static String ERROR_FOR_QUERY = "SQL query error: {}, parameters: {}.";
    private final static String ERROR_FOR_LIST = "SQL query list error: {}, parameters: {}.";
    private final static String ERROR_FOR_ONE = "SQL query one error: {}, parameters: {}.";

    public JdbcSessionImpl() {
    }

    private Connection openConnection() {
        Connection connection = null;
        return null;
    }

    @Override
    public int insert(String sql) { return update(sql); }

    @Override
    public int insert(String sql, Object[] parameters) { return update(sql, parameters); }

    @Override
    public int update(String sql) {
        Statement stmt = null;
        try {
            Connection connection = openConnection();
            stmt = connection.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw Exception2.with(e, "Insert error: {}", sql);
        } finally {
            closeContext(stmt);
        }
    }

    @Override
    public int update(String sql, Object[] parameters) {
        PreparedStatement stmt = null;
        try {
            Connection connection = openConnection();
            stmt = connection.prepareStatement(sql);
            setObjectParameters(stmt, parameters);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw Exception2.with(e, "SQL DML error: {}.", sql);
        } finally {
            closeContext(stmt);
        }
    }

    @Override
    public int delete(String sql) { return update(sql); }

    @Override
    public int delete(String sql, Object[] parameters) { return update(sql, parameters); }

    @Override
    public <T> T selectOne(String sql, RowMapper<T> mapper) {
        Connection connection = openConnection();
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet resultSet = stmt.executeQuery(sql)) {
                T result = mapper.doMap(resultSet);
                if (resultSet.next()) {
                    throw Exception2.with(ERROR_FOR_ONE, sql, "[]");
                }
                return result;
            }
        } catch (SQLException e) {
            throw Exception2.with(ERROR_FOR_ONE, sql, "[]");
        }
    }

    @Override
    public <T> List<T> selectAll(String sql, RowMapper<T> mapper) {
        return doExecuteQuery(sql, mapper, (resultSet, rowMapper) -> {
            List<T> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(rowMapper.doMap(resultSet));
            }
            return resultList;
        }, ERROR_FOR_LIST);
    }

    @Override
    public <T> T select(String sql, ResultExtractor<T> extractor) {
        return doExecuteQuery(sql, extractor, (set, ext) -> ext.extract(set), ERROR_FOR_QUERY);
    }

    @Override
    public <T> T selectOne(String sql, Object[] parameters, RowMapper<T> mapper) {
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setObjectParameters(stmt, parameters);
            try (ResultSet resultSet = stmt.executeQuery()) {
                T result = mapper.doMap(resultSet);
                if (resultSet.next()) {
                    throw Exception2.with(ERROR_FOR_ONE, sql, Arrays.toString(parameters));
                }
                return result;
            }
        } catch (SQLException e) {
            throw Exception2.with(e, ERROR_FOR_ONE, sql, Arrays.toString(parameters));
        }
    }

    @Override
    public <T> List<T> selectAll(String sql, Object[] parameters, RowMapper<T> mapper) {
        return doExecuteQuery(sql, parameters, mapper, (resultSet, rowMapper) -> {
            List<T> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(rowMapper.doMap(resultSet));
            }
            return resultList;
        }, ERROR_FOR_LIST);
    }

    @Override
    public <T> T select(String sql, Object[] parameters, ResultExtractor<T> extractor) {
        return doExecuteQuery(sql, parameters, extractor, (set, ext) -> ext.extract(set), ERROR_FOR_QUERY);
    }

    private <T, V> T doExecuteQuery(
        String sql, Object[] parameters, V extra, ThrowingBiFunction<ResultSet, V, T> extractor, String errorTemplate
    ) {
        if (parameters == null || parameters.length == 0) {
            return doExecuteQuery(sql, extra, extractor, errorTemplate);
        }
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setObjectParameters(stmt, parameters);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return extractor.apply(resultSet, extra);
            }
        } catch (SQLException e) {
            throw Exception2.with(e, errorTemplate, sql, Arrays.toString(parameters));
        }
    }

    private <T, V> T doExecuteQuery(
        String sql, V extra, ThrowingBiFunction<ResultSet, V, T> extractor, String errorTemplate
    ) {
        Connection connection = openConnection();
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet resultSet = stmt.executeQuery(sql)) {
                return extractor.apply(resultSet, extra);
            }
        } catch (SQLException e) {
            throw Exception2.with(e, errorTemplate, sql, "[]");
        }
    }

    private interface ThrowingBiFunction<T, V, R> {

        R apply(T t, V v) throws SQLException;
    }

    @Override
    public void close() throws IOException {
        //
    }

    private static void setObjectParameters(PreparedStatement stmt, Object[] parameters) throws SQLException {
        for (int i = 0, length = parameters.length; i < length; i++) {
            stmt.setObject(i + 1, parameters[i]);
        }
    }

    private static void closeContext(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
