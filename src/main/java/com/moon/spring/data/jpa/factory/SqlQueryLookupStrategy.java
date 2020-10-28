package com.moon.spring.data.jpa.factory;

import com.moon.data.annotation.SqlSelect;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

/**
 * @author moonsky
 */
public class SqlQueryLookupStrategy implements QueryLookupStrategy {

    private final QueryLookupStrategy lookupStrategy;
    private final QueryExtractor extractor;
    private final EntityManager em;
    private final JpaRecordRepositoryMetadata metadata;

    public SqlQueryLookupStrategy(
        QueryLookupStrategy lookupStrategy,
        EntityManager em,
        QueryExtractor extractor,
        JpaRecordRepositoryMetadata metadata
    ) {
        this.metadata = metadata;
        this.lookupStrategy = lookupStrategy;
        this.extractor = extractor;
        this.em = em;
    }

    public JpaRecordRepositoryMetadata getRepositoryContextMetadata() { return metadata; }

    @Override
    public RepositoryQuery resolveQuery(
        Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries
    ) {
        try {
            return lookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
        } catch (RuntimeException e) {
            return resolveQuery4Sql(method, metadata, factory, namedQueries, e);
        }
    }

    private RepositoryQuery resolveQuery4Sql(
        Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries queries, Exception e
    ) {
        SqlSelect sql = method.getAnnotation(SqlSelect.class);
        sql = sql == null ? method.getDeclaredAnnotation(SqlSelect.class) : sql;
        requireAnnotated(sql == null, method, e);
        JpaRecordRepositoryMetadata ctxMetadata = getRepositoryContextMetadata();
        if (RepositoryUtil.isPresentJdbcTemplateBean(ctxMetadata)) {
            return new RecordJdbcRepositoryQuery(sql, method, metadata, factory, extractor, em, ctxMetadata);
        }
        return new RecordJpaRepositoryQuery(sql, method, metadata, factory, extractor, em, ctxMetadata);
    }

    final static String MSG_TEMPLATE = "解析错误：{}, 请检查语法或使用注解 @" + SqlSelect.class.getSimpleName() + "(..).";

    private void requireAnnotated(boolean doThrow, Method method, Exception e) {
        if (doThrow) {
            String message = method.getDeclaringClass().getSimpleName() + "#" + method.getName();
            throw new IllegalArgumentException(MSG_TEMPLATE.replace("{}", message), e);
        }
    }
}
