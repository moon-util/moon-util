package com.moon.accessor.config;

import java.util.Objects;

/**
 * @author benshaoye
 */
public class Configuration {

    private final ConnectionFactory connectionFactory;
    private Settings settings;

    public Configuration(ConnectionFactory connectionFactory) {
        Objects.requireNonNull(connectionFactory, "The 'connectionFactory' must not be null.");
        this.connectionFactory = connectionFactory;
    }

    public ConnectionFactory getConnectionFactory() { return connectionFactory; }

    public Settings getSettings() { return settings == null ? Settings.DEFAULT : settings; }
}
