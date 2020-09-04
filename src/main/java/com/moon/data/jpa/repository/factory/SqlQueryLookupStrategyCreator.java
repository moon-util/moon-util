package com.moon.data.jpa.repository.factory;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.ref.FinalAccessor;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

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
                if (strategy.isLoadSuccessful(method.getName(), method.getParameterTypes())) {
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
    private static enum CreateStrategy {
        OLD(FinalAccessor.of("create"),
            "javax.persistence.EntityManager",
            "org.springframework.data.repository.query.QueryLookupStrategy$Key",
            "org.springframework.data.jpa.provider.QueryExtractor",
            "org.springframework.data.repository.query.QueryMethodEvaluationContextProvider",
            "org.springframework.data.jpa.repository.query.EscapeCharacter") {
            @Override
            Object[] transform(
                EntityManager em,
                QueryLookupStrategy.Key key,
                QueryExtractor extractor,
                QueryMethodEvaluationContextProvider provider,
                EscapeCharacter escape
            ) { return toObjects(em, key, extractor, provider, escape); }
        },
        NEW(FinalAccessor.of("create"),
            "javax.persistence.EntityManager",
            "org.springframework.data.jpa.repository.query.JpaQueryMethodFactory",
            "org.springframework.data.repository.query.QueryLookupStrategy$Key",
            "org.springframework.data.repository.query.QueryMethodEvaluationContextProvider",
            "org.springframework.data.jpa.repository.query.EscapeCharacter") {
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
        ;

        final String methodName;
        final Class[] classes;
        final boolean loadSuccessful;

        CreateStrategy(FinalAccessor<String> method, String... parametersType) {
            this.methodName = method.get();
            boolean successful = true;
            Class[] classArr = new Class[parametersType.length];
            for (int i = 0; i < parametersType.length; i++) {
                Class type = ClassUtil.forNameOrNull(parametersType[i]);
                if (type == null) {
                    successful = false;
                    classArr = null;
                    break;
                } else {
                    classArr[i] = type;
                }
            }
            this.classes = classArr;
            this.loadSuccessful = successful;
        }

        abstract Object[] transform(
            EntityManager em,
            QueryLookupStrategy.Key key,
            QueryExtractor extractor,
            QueryMethodEvaluationContextProvider provider,
            EscapeCharacter escape
        );

        static Object[] toObjects(Object... objects) { return objects; }

        public boolean isLoadSuccessful(String methodName, Class... types) {
            if (loadSuccessful && Objects.equals(methodName, this.methodName)) {
                if (types != null && types.length == classes.length) {
                    for (int i = 0; i < types.length; i++) {
                        if (types[i] != classes[i]) {
                            return false;
                        }
                    }
                    return true;
                }
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
