package com.moon.accessor.config;

import com.moon.accessor.exception.Exception2;
import com.moon.accessor.exception.SqlException;
import com.moon.accessor.dialect.SQLDialect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class Configuration {

    private final SQLDialect dialect;
    private final DataSource dataSource;
    private ConnectionGetter connectionGetter;
    private ConnectionReleaser connectionReleaser;

    public Configuration(DataSource dataSource, SQLDialect dialect) {
        this.dataSource = dataSource;
        this.dialect = dialect;
    }

    public DataSource getDataSource() { return dataSource; }

    private ConnectionGetter getConnectionGetter() {
        return connectionGetter == null ? ds -> {
            try {
                return ds.getConnection();
            } catch (SQLException e) {
                throw Exception2.with(e);
            }
        } : connectionGetter;
    }

    public void setConnectionGetter(ConnectionGetter connectionGetter) {
        this.connectionGetter = connectionGetter;
    }

    public ConnectionReleaser getConnectionReleaser() {
        return connectionReleaser == null ? (conn, ds) -> {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        } : connectionReleaser;
    }

    private void setConnectionReleaser(ConnectionReleaser connectionReleaser) {
        this.connectionReleaser = connectionReleaser;
    }

    public void runWithAutoRelease(Consumer<Connection> consumer) throws SqlException {
        Connection connection = getConnectionGetter().getConnection(getDataSource());
        try {
            consumer.accept(connection);
        } finally {
            getConnectionReleaser().release(connection, getDataSource());
        }
    }
}
