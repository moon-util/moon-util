package com.moon.accessor.session;

import java.io.Closeable;
import java.util.List;

/**
 * @author benshaoye
 */
public interface JdbcSession extends Closeable {

    int insert(String sql);

    int insert(String sql, Object[] parameters);

    int update(String sql);

    int update(String sql, Object[] parameters);

    int delete(String sql);

    int delete(String sql, Object[] parameters);

    <T> T select(String sql, Object[] parameters, ResultExtractor<T> extractor);

    <T> List<T> selectAll(String sql, Object[] parameters, RowMapper<T> mapper);

    <T> T selectOne(String sql, Object[] parameters, RowMapper<T> mapper);
}
