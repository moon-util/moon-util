package com.moon.accessor.session;

import com.moon.accessor.config.ConfigurationContext;
import com.moon.accessor.config.Configuration;

/**
 * @author benshaoye
 */
@SuppressWarnings("unused")
public class SessionFactoryImpl extends ConfigurationContext implements SessionFactory {

    public SessionFactoryImpl(Configuration config) { super(config); }

    @Override
    public JdbcSession openJdbcSession() {
        return new JdbcSessionImpl(getConfiguration());
    }

    @SuppressWarnings("all")
    @Override
    public <T extends DSLSession> T openDSLSession() {
        return DSLSession2.newDSLSession(getConfiguration());
    }
}
