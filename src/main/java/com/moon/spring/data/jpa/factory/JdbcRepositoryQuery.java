package com.moon.spring.data.jpa.factory;

import com.moon.data.annotation.SqlSelect;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

/**
 * @author moonsky
 */
public class JdbcRepositoryQuery extends AbstractRepositoryQuery {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRepositoryQuery(
        SqlSelect sql,
        Method method,
        RepositoryMetadata metadata,
        ProjectionFactory factory,
        QueryExtractor extractor,
        EntityManager em,
        RepositoryContextMetadata repositoryContextMetadata
    ) {
        super(sql, method, metadata, factory, extractor, em, repositoryContextMetadata);
        this.jdbcTemplate = repositoryContextMetadata.getBean(JdbcTemplate.class);
    }
}
