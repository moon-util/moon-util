package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.annotation.JdbcSelect;
import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @author moonsky
 */
public abstract class AbstractRepositoryQuery implements RepositoryQuery {

    private final RepositoryMetadata metadata;
    private final ProjectionFactory factory;
    private final QueryExtractor extractor;
    private final QueryMethod queryMethod;
    private final EntityManager em;
    private final Method method;
    private final JdbcSelect sql;
    private final RepositoryContextMetadata repositoryContextMetadata;

    public AbstractRepositoryQuery(
        JdbcSelect sql,
        Method method,
        RepositoryMetadata metadata,
        ProjectionFactory factory,
        QueryExtractor extractor,
        EntityManager em,
        RepositoryContextMetadata repositoryContextMetadata
    ) {
        this.em = em;
        this.method = method;
        this.factory = factory;
        this.metadata = metadata;
        this.extractor = extractor;
        this.sql = Objects.requireNonNull(sql);
        this.queryMethod = new QueryMethod(method, metadata, factory);
        this.repositoryContextMetadata = repositoryContextMetadata;
        Class returnCls = method.getReturnType();
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) returnType;
            Type rawType = paramType.getRawType();
            Type ownerType = paramType.getOwnerType();
            Type[] argsType = paramType.getActualTypeArguments();

        }

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
        }
    }

    @Override
    public Object execute(Object[] parameters) {
        Query query = em.createNativeQuery("SELECT * FROM `tb_user_part_details`");
        if (query instanceof NativeQuery) {
            NativeQuery nativeQuery = (NativeQuery) query;
            // nativeQuery.
            List list = query.getResultList();
            return list;
        } else {
            List list = query.getResultList();
            return list;
        }
    }

    @Override
    public QueryMethod getQueryMethod() { return queryMethod; }
}
