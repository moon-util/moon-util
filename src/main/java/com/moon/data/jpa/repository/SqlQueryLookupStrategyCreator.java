package com.moon.data.jpa.repository;

import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.*;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author benshaoye
 */
public class SqlQueryLookupStrategyCreator {

    private final static Method CREATOR_METHOD;
    private final static CreateStrategy STRATEGY;

    static {
        Method resultMethod = null;
        CreateStrategy resultStrategy = null;
        CreateStrategy[] strategies = CreateStrategy.values();
        Method[] methods = JpaQueryLookupStrategy.class.getDeclaredMethods();
        methods:
        for (Method method : methods) {
            for (CreateStrategy strategy : strategies) {
                if (strategy.isMethodNameOf(method.getName()) && strategy.isMatches(method.getParameterTypes())) {
                    resultStrategy = strategy;
                    resultMethod = method;
                    break methods;
                }
            }
        }
        STRATEGY = Objects.requireNonNull(resultStrategy);
        CREATOR_METHOD = Objects.requireNonNull(resultMethod);
    }

    static QueryLookupStrategy create(
        EntityManager em,
        QueryLookupStrategy.Key key,
        QueryExtractor extractor,
        QueryMethodEvaluationContextProvider provider,
        EscapeCharacter escape
    ) {
        try {
            return (QueryLookupStrategy) CREATOR_METHOD.invoke(null,
                STRATEGY.transform(em, key, extractor, provider, escape));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    private enum CreateStrategy {
        OLD("create",
            EntityManager.class,
            QueryLookupStrategy.Key.class,
            QueryExtractor.class,
            QueryMethodEvaluationContextProvider.class,
            EscapeCharacter.class) {
            @Override
            Object[] transform(
                EntityManager em,
                QueryLookupStrategy.Key key,
                QueryExtractor extractor,
                QueryMethodEvaluationContextProvider provider,
                EscapeCharacter escape
            ) {
                return toObjects(em, new MoonJpaQueryMethodFactory(extractor), key, provider, escape);
            }
        },
        NEW("create",
            EntityManager.class,
            JpaQueryMethodFactory.class,
            QueryLookupStrategy.Key.class,
            QueryMethodEvaluationContextProvider.class,
            EscapeCharacter.class) {
            @Override
            Object[] transform(
                EntityManager em,
                QueryLookupStrategy.Key key,
                QueryExtractor extractor,
                QueryMethodEvaluationContextProvider provider,
                EscapeCharacter escape
            ) { return toObjects(em, key, extractor, provider, escape); }
        };

        final String methodName;
        final Class[] classes;

        CreateStrategy(String methodName, Class... classes) {
            this.methodName = methodName;
            this.classes = classes;
        }

        abstract Object[] transform(
            EntityManager em,
            QueryLookupStrategy.Key key,
            QueryExtractor extractor,
            QueryMethodEvaluationContextProvider provider,
            EscapeCharacter escape
        );

        static Object[] toObjects(Object... objects) { return objects; }

        boolean isMethodNameOf(String methodName) { return Objects.equals(methodName, this.methodName); }

        boolean isMatches(Class... types) {
            if (types != null && types.length == classes.length) {
                for (int i = 0; i < types.length; i++) {
                    if (types[i] != classes[i]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private static final class MoonJpaQueryMethodFactory implements JpaQueryMethodFactory {

        private final QueryExtractor extractor;

        public MoonJpaQueryMethodFactory(QueryExtractor extractor) {
            Objects.requireNonNull(extractor, "QueryExtractor must not be null");
            this.extractor = extractor;
        }

        @Override
        public JpaQueryMethod build(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
            return new MoonJpaQueryMethod(method, metadata, factory, this.extractor);
        }
    }

    private static final class MoonJpaQueryMethod extends JpaQueryMethod {

        protected MoonJpaQueryMethod(
            Method method, RepositoryMetadata metadata, ProjectionFactory factory, QueryExtractor extractor
        ) { super(method, metadata, factory, extractor); }
    }
}
