package com.moon.accessor.config;

import javax.sql.DataSource;

/**
 * @author benshaoye
 */
public class DSLConfiguration {

    private final DataSource dataSource;
    private ConnectionGetter connectionGetter;
    private ConnectionReleaser connectionReleaser;

    public DSLConfiguration(DataSource dataSource) { this.dataSource = dataSource; }

    public DataSource getDataSource() { return dataSource; }

    public ConnectionGetter getConnectionGetter() {
        return connectionGetter == null ? DataSource::getConnection : connectionGetter;
    }

    public void setConnectionGetter(ConnectionGetter connectionGetter) {
        this.connectionGetter = connectionGetter;
    }

    public ConnectionReleaser getConnectionReleaser() {
        return connectionReleaser == null ? (conn, ds) -> conn.close() : connectionReleaser;
    }

    public void setConnectionReleaser(ConnectionReleaser connectionReleaser) {
        this.connectionReleaser = connectionReleaser;
    }
}
