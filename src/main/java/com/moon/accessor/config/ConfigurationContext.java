package com.moon.accessor.config;

/**
 * @author benshaoye
 */
public abstract class ConfigurationContext {

    private final Configuration configuration;

    public ConfigurationContext(Configuration configuration) {
        this.configuration = configuration;
    }

    protected final Configuration getConfig(){return getConfiguration();}

    public Configuration getConfiguration() { return configuration; }
}
