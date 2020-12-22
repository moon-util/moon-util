package com.moon.data.jdbc.dialect;

import java.util.Set;

/**
 * @author benshaoye
 */
public interface DialectDescriptor {

    /**
     * 返回相应数据库的保留字
     *
     * @return 保留字列表
     */
    Set<String> getReservedWords();
}
