package com.moon.spring.data.jpa.factory;

/**
 * @author benshaoye
 */
enum JdbcStrategy {
    /**
     * 查询
     */
    SELECT,
    /**
     * 删除
     */
    DELETE,
    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE
}
