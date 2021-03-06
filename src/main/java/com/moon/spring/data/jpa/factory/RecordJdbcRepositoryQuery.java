package com.moon.spring.data.jpa.factory;

import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

/**
 * @author moonsky
 */
public class RecordJdbcRepositoryQuery extends AbstractRepositoryQuery {

    private final JdbcTemplate jdbcTemplate;

    public RecordJdbcRepositoryQuery(
        JdbcAttributes sql,
        Method method,
        RepositoryMetadata metadata,
        ProjectionFactory factory,
        QueryExtractor extractor,
        EntityManager em,
        JpaRecordRepositoryMetadata jpaRecordRepositoryMetadata
    ) {
        super(sql, method, metadata, factory, extractor, em, jpaRecordRepositoryMetadata);
        this.jdbcTemplate = (JdbcTemplate) jpaRecordRepositoryMetadata.getBean(JdbcTemplate.class);
    }
}
