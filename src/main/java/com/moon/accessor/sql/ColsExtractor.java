package com.moon.accessor.sql;

import com.moon.accessor.util.Indexer;

import java.sql.ResultSet;
import java.util.Objects;

/**
 * select 列名提取器：
 * <p>
 * 从 select 查询中提取查询结果的列名，类似 count(*)、
 * max(u.name) 以及单列子查询这种数据库能正确执行，但不能映射为某个属性名，
 * 所有必须要有别名，否则会忽略
 *
 * @author benshaoye
 */
public class ColsExtractor {

    private final char[] select;
    private final Indexer indexer;

    public ColsExtractor(String select) {
        Objects.requireNonNull(select, "SELECT 语句不能为 null。");
        String trimmed = select.trim();
        char[] chars = trimmed.toCharArray();
        this.indexer = new Indexer(chars.length);
        this.select = chars;
        ResultSet set = null;
    }
}
