package com.moon.accessor.session;

/**
 * @author benshaoye
 */
public class SessionFactoryImpl implements SessionFactory {

    public SessionFactoryImpl() {
    }

    @Override
    public JdbcSession openJdbcSession() {
        return new JdbcSessionImpl();
    }

    @Override
    public <T extends DSLSession> T openDSLSession() {
        return null;
    }
}
