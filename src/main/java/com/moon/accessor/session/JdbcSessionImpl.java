package com.moon.accessor.session;

import com.moon.accessor.config.Configuration;
import com.moon.accessor.config.ConfigurationContext;
import com.moon.accessor.config.ConnectionFactory;
import com.moon.accessor.exception.Exception2;
import com.moon.accessor.function.ThrowingBiApplier;
import com.moon.accessor.function.ThrowingBiConsumer;
import com.moon.accessor.param.Parameter2;
import com.moon.accessor.param.ParameterSetter;
import com.moon.accessor.result.Result2;
import com.moon.accessor.result.ResultExtractor;
import com.moon.accessor.result.RowMapper;
import com.moon.accessor.util.Collect2;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

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
    public int update(String sql) { return doUpdateSQL(sql); }

    @Override
    public int update(String sql, Object[] parameters) {
        return doUpdateSQL(sql, parameters, Parameter2::setAll);
    }

    @Override
    public int update(String sql, ParameterSetter[] parameters) {
        return doUpdateSQL(sql, parameters, Parameter2::setAll);
    }

    @Override
    public int update(String sql, Collection<ParameterSetter> parameters) {
        return doUpdateSQL(sql, parameters, Parameter2::setAll);
    }

    private int doUpdateSQL(String sql) {
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw Exception2.with(e, "SQL DML error: {}.", sql);
        } finally {
            releaseConnection(connection);
        }
    }

    private <T> int doUpdateSQL(
        String sql, T[] parameters, ThrowingBiConsumer<PreparedStatement, T[]> parameterSetter
    ) {
        if (parameters == null || parameters.length == 0) {
            return doUpdateSQL(sql);
        }
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            parameterSetter.accept(stmt, parameters);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw Exception2.with(e, "SQL DML error: {}, parameters: {}.", sql, stringify(parameters));
        } finally {
            releaseConnection(connection);
        }
    }

    private int doUpdateSQL(
        String sql,
        Collection<ParameterSetter> parameters,
        ThrowingBiConsumer<PreparedStatement, Collection<ParameterSetter>> parameterSetter
    ) {
        if (parameters == null || parameters.isEmpty()) {
            return doUpdateSQL(sql);
        }
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            parameterSetter.accept(stmt, parameters);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw Exception2.with(e, "SQL DML error: {}, parameters: {}.", sql, parameters.toString());
        } finally {
            releaseConnection(connection);
        }
    }

    @Override
    public int[] updateBatch(String sql, Iterable<Object[]> parameters) {
        if (parameters instanceof Collection) {
            return updateBatchCollect(sql, (Collection<Object[]>) parameters);
        }
        return updateBatchIterable(sql, parameters);
    }

    private int[] updateBatchCollect(String sql, Collection<Object[]> parameters) {
        if (Collect2.isEmpty(parameters)) {
            return new int[0];
        }
        if (parameters instanceof List) {
            return updateBatchList(sql, (List<Object[]>) parameters);
        }
        return updateBatchIterable(sql, parameters);
    }

    private int[] updateBatchList(String sql, List<Object[]> parameters) {
        if (parameters.size() == 1) {
            return new int[]{update(sql, parameters.get(0))};
        }
        return updateBatchIterable(sql, parameters);
    }

    private int[] updateBatchIterable(String sql, Iterable<Object[]> parameters) {
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Object[] params : parameters) {
                Parameter2.setAll(stmt, params);
                stmt.addBatch();
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            for (Object[] parameter : parameters) {
                joiner.add(stringify(parameter));
            }
            throw Exception2.with(e, "SQL DML error: {}, parameters: {}.", sql, joiner.toString());
        } finally {
            releaseConnection(connection);
        }
    }

    @Override
    public <T> T selectOne(String sql, RowMapper<T> mapper) {
        return doExecuteQuery(sql, mapper, Result2::atMost1, ERROR_FOR_ONE);
    }

    @Override
    public <T> List<T> selectAll(String sql, RowMapper<T> mapper) {
        return doExecuteQuery(sql, mapper, Result2::listAll, ERROR_FOR_LIST);
    }

    @Override
    public <T> T select(String sql, ResultExtractor<T> extractor) {
        return doExecuteQuery(sql, extractor, Result2::extract, ERROR_FOR_QUERY);
    }

    @Override
    public <T> T selectOne(String sql, Object[] parameters, RowMapper<T> mapper) {
        return doExecuteQuery(sql, parameters, mapper, Result2::atMost1, ERROR_FOR_ONE);
    }

    @Override
    public <T> List<T> selectAll(String sql, Object[] parameters, RowMapper<T> mapper) {
        return doExecuteQuery(sql, parameters, mapper, Result2::listAll, ERROR_FOR_LIST);
    }

    @Override
    public <T> T select(String sql, Object[] parameters, ResultExtractor<T> extractor) {
        return doExecuteQuery(sql, parameters, extractor, Result2::extract, ERROR_FOR_QUERY);
    }

    private <T, V> T doExecuteQuery(
        String sql, Object[] parameters, V extra, ThrowingBiApplier<ResultSet, V, T> extractor, String errorMessage
    ) {
        if (parameters == null || parameters.length == 0) {
            return doExecuteQuery(sql, extra, extractor, errorMessage);
        }
        Connection connection = openConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Parameter2.setAll(stmt, parameters);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return extractor.apply(resultSet, extra);
            }
        } catch (SQLException e) {
            throw Exception2.with(e, errorMessage, sql, stringify(parameters));
        } finally {
            releaseConnection(connection);
        }
    }

    private <T, V> T doExecuteQuery(
        String sql, V extra, ThrowingBiApplier<ResultSet, V, T> extractor, String errorMessage
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

    private static String stringify(Object[] parameters) { return Arrays.toString(parameters); }
}
