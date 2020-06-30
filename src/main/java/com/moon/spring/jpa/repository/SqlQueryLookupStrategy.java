package com.moon.spring.jpa.repository;

import com.moon.spring.jpa.annotation.SqlStatement;
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

    public SqlQueryLookupStrategy(QueryLookupStrategy lookupStrategy, EntityManager em, QueryExtractor extractor) {
        this.lookupStrategy = lookupStrategy;
        this.extractor = extractor;
        this.em = em;
    }

    @Override
    public RepositoryQuery resolveQuery(
        Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries
    ) {
        try {
            return lookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
        } catch (RuntimeException e) {
            RepositoryQuery query = resolveQuery4Sql(method, metadata, factory, namedQueries);
            return query == null ? doThrowException(method, e) : query;
        }
    }

    private SqlRepositoryQuery resolveQuery4Sql(
        Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries queries
    ) {
        SqlStatement sql = method.getAnnotation(SqlStatement.class);
        sql = sql == null ? method.getDeclaredAnnotation(SqlStatement.class) : sql;
        return sql == null ? null : new SqlRepositoryQuery(sql, method, metadata, factory, extractor, em);
    }

    private RepositoryQuery doThrowException(Method method, Exception e) {
        String message = method.getDeclaringClass().getSimpleName() + "#" + method.getName();
        message = "解析错误：" + message + ", 请检查语法或使用注解 @Sql(..).";
        throw new IllegalArgumentException(message, e);
    }
}
