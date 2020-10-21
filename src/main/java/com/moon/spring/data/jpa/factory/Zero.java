package com.moon.spring.data.jpa.factory;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * @author moonsky
 */
final class Zero {

    enum Env implements Environment {
        ENV;

        static final String[] EMPTY = new String[0];

        @Override
        public String[] getActiveProfiles() { return EMPTY; }

        @Override
        public String[] getDefaultProfiles() { return EMPTY; }

        @Override
        public boolean acceptsProfiles(String... profiles) { return false; }

        @Override
        public boolean acceptsProfiles(Profiles profiles) { return false; }

        @Override
        public boolean containsProperty(String key) { return false; }

        @Override
        public String getProperty(String key) { return null; }

        @Override
        public String getProperty(String key, String defaultValue) { return defaultValue; }

        @Override
        public <T> T getProperty(String key, Class<T> targetType) { return null; }

        @Override
        public <T> T getProperty(String key, Class<T> targetType, T v) { return v; }

        @Override
        public String getRequiredProperty(String key) { throw new IllegalStateException(key); }

        @Override
        public <T> T getRequiredProperty(String key, Class<T> targetType) { throw new IllegalStateException(key); }

        @Override
        public String resolvePlaceholders(String text) { return null; }

        @Override
        public String resolveRequiredPlaceholders(String text) { throw new IllegalStateException(text); }
    }
}
