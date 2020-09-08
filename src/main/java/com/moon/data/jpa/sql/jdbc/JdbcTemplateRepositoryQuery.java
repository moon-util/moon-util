package com.moon.data.jpa.sql.jdbc;

import com.moon.data.annotation.SqlSelect;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;

/**
 * @author moonsky
 */
public class JdbcTemplateRepositoryQuery implements RepositoryQuery {

    private final JdbcTemplate template;
    private final SqlSelect select;
    private final Method method;

    public JdbcTemplateRepositoryQuery(JdbcTemplate template, SqlSelect select, Method method) {
        this.template = template;
        this.select = select;
        this.method = method;
    }

    @Override
    public Object execute(Object[] parameters) {
        return null;
    }

    @Override
    public QueryMethod getQueryMethod() {
        return null;
    }
}
