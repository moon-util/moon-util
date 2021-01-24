package com.moon.accessor.session;

/**
 * @author benshaoye
 */
public interface SessionFactory {

    /**
     * 直接访问 JDBC 资源
     *
     * @return JDBC
     */
    JdbcSession openJdbcSession();

    /**
     * DSL 语法
     *
     * @param <T> DSL 实现
     *
     * @return DSL
     */
    <T extends DSLSession> T openDSLSession();
}
