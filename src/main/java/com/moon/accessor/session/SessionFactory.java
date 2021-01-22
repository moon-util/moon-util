package com.moon.accessor.session;

/**
 * @author benshaoye
 */
public interface SessionFactory {

    JdbcSession openJdbcSession();
}
