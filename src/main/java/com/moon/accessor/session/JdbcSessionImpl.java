package com.moon.accessor.session;

import com.moon.accessor.config.ConfigurationContext;
import com.moon.accessor.config.Configuration;
import com.moon.accessor.config.ConnectionFactory;
import com.moon.accessor.exception.Exception2;
import com.moon.accessor.result.Result2;
import com.moon.accessor.result.ResultExtractor;
import com.moon.accessor.result.RowMapper;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author benshaoye
 */
public class JdbcSessionImpl extends ConfigurationContext implements JdbcSession {

    private final static String ERROR_FOR_QUERY = "SQL query error: {}, parameters: {}.";
    private final static String ERROR_FOR_LIST = "SQL query list error: {}, parameters: {}.";
    private final static String ERROR_FOR_ONE = "SQL query one error: {}, parameters: {}.";

    public JdbcSessionImpl(Configuration configuration) { super(configuration); }

    private ConnectionFactory getConnectionFactory() {
        return getConfiguration().getConnectionFactory();
    }

    private Connection openConnection() {
        try {
            return getConnectionFactory().getConnection();
        } catch (SQLException e) {
            throw Exception2.with(e);
        }
    }

    private void releaseConnection(Connection connection) {
        try {
            getConnectionFactory().release(connection);
        } catch (Exception e) {
            throw Exception2.with(e);
        }
    }

    @Override
    public int insert(String sql) { return update(sql); }

    @Override
    public int insert(String sql, Object[] parameters) { return update(sql, parameters); }

    @Override
    public int update(String sql) {
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw Exception2.with(e, "SQL DML error: {}.", sql);
        } finally {
            releaseConnection(connection);
        }
    }

    @Override
    public int update(String sql, Object[] parameters) {
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setObjectParameters(stmt, parameters);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw Exception2.with(e, "SQL DML error: {}, parameters: {}.", sql, toArr(parameters));
        } finally {
            releaseConnection(connection);
        }
    }

    @Override
    public int delete(String sql) { return update(sql); }

    @Override
    public int delete(String sql, Object[] parameters) { return update(sql, parameters); }

    @Override
    public <T> T selectOne(String sql, RowMapper<T> mapper) {
        return doExecuteQuery(sql, mapper, Result2::extractOne, ERROR_FOR_ONE);
    }

    @Override
    public <T> List<T> selectAll(String sql, RowMapper<T> mapper) {
        return doExecuteQuery(sql, mapper, Result2::extractList, ERROR_FOR_LIST);
    }

    @Override
    public <T> T select(String sql, ResultExtractor<T> extractor) {
        return doExecuteQuery(sql, extractor, Result2::extract, ERROR_FOR_QUERY);
    }

    @Override
    public <T> T selectOne(String sql, Object[] parameters, RowMapper<T> mapper) {
        return doExecuteQuery(sql, parameters, mapper, Result2::extractOne, ERROR_FOR_ONE);
    }

    @Override
    public <T> List<T> selectAll(String sql, Object[] parameters, RowMapper<T> mapper) {
        return doExecuteQuery(sql, parameters, mapper, Result2::extractList, ERROR_FOR_LIST);
    }

    @Override
    public <T> T select(String sql, Object[] parameters, ResultExtractor<T> extractor) {
        return doExecuteQuery(sql, parameters, extractor, Result2::extract, ERROR_FOR_QUERY);
    }

    private <T, V> T doExecuteQuery(
        String sql, Object[] parameters, V extra, ThrowingBiFunction<ResultSet, V, T> extractor, String errorMessage
    ) {
        if (parameters == null || parameters.length == 0) {
            return doExecuteQuery(sql, extra, extractor, errorMessage);
        }
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setObjectParameters(stmt, parameters);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return extractor.apply(resultSet, extra);
            }
        } catch (SQLException e) {
            throw Exception2.with(e, errorMessage, sql, toArr(parameters));
        } finally {
            releaseConnection(connection);
        }
    }

    private <T, V> T doExecuteQuery(
        String sql, V extra, ThrowingBiFunction<ResultSet, V, T> extractor, String errorMessage
    ) {
        Connection connection = openConnection();
        try (@SuppressWarnings("all") Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {
            return extractor.apply(resultSet, extra);
        } catch (SQLException e) {
            throw Exception2.with(e, errorMessage, sql, "[]");
        } finally {
            releaseConnection(connection);
        }
    }

    @Override
    public void close() throws IOException {
        //
    }

    private static void setObjectParameters(PreparedStatement stmt, Object[] parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            stmt.setObject(i + 1, parameters[i]);
        }
    }

    private static String toArr(Object[] parameters) {
        return Arrays.toString(parameters);
    }

    /**
     * 两参数函数
     *
     * @param <T> 参数 1
     * @param <V> 参数 2
     * @param <R> 返回类型
     */
    private interface ThrowingBiFunction<T, V, R> {

        /**
         * 执行函数
         *
         * @param t 参数 1
         * @param v 参数 2
         *
         * @return 返回
         *
         * @throws SQLException SQLException
         */
        R apply(T t, V v) throws SQLException;
    }
}
