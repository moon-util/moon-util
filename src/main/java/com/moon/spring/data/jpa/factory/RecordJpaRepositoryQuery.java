package com.moon.spring.data.jpa.factory;

import com.moon.data.annotation.SqlSelect;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

/**
 * @author moonsky
 */
public class RecordJpaRepositoryQuery extends AbstractRepositoryQuery {

    public RecordJpaRepositoryQuery(
        SqlSelect sql,
        Method method,
        RepositoryMetadata metadata,
        ProjectionFactory factory,
        QueryExtractor extractor,
        EntityManager em,
        JpaRecordRepositoryMetadata jpaRecordRepositoryMetadata
    ) { super(sql, method, metadata, factory, extractor, em, jpaRecordRepositoryMetadata); }
}
