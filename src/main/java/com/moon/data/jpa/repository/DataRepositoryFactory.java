package com.moon.data.jpa.repository;

import com.moon.core.lang.ArrayUtil;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * @author moonsky
 */
public class DataRepositoryFactory extends JpaRepositoryFactory {

    protected final EntityManager em;
    private final QueryExtractor extractor;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    public DataRepositoryFactory(EntityManager em) {
        super(em);
        this.em = em;
        this.extractor = PersistenceProvider.fromEntityManager(em);
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);
        this.escapeCharacter = escapeCharacter;
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(
        RepositoryInformation information, EntityManager em
    ) {
        JpaEntityInformation ei = getEntityInformation(information.getDomainType());
        return new DataRepositoryImpl<>(ei, em);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) { return DataRepositoryImpl.class; }


    @Override
    public <T> T getRepository(Class<T> repositoryInterface, RepositoryComposition.RepositoryFragments fragments) {
        return super.getRepository(repositoryInterface, fragments);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
        QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider provider
    ) {
        return Optional.of(create(em, key, extractor, provider, escapeCharacter));
    }

    /**
     * create {@link QueryLookupStrategy}
     *
     * @param em
     * @param key
     * @param extractor
     * @param provider
     * @param escape
     *
     * @return
     *
     * @see SqlQueryLookupStrategy
     * @see JpaQueryLookupStrategy#create
     */
    public static QueryLookupStrategy create(
        EntityManager em,
        @Nullable QueryLookupStrategy.Key key,
        QueryExtractor extractor,
        QueryMethodEvaluationContextProvider provider,
        EscapeCharacter escape
    ) {
        QueryLookupStrategy strategy = QueryLookupStrategyCreator.create(em, key, extractor, provider, escape);
        switch (key != null ? key : QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND) {
            case CREATE:
            case CREATE_IF_NOT_FOUND:
                strategy = new SqlQueryLookupStrategy(strategy, em, extractor);
                break;
        }
        return strategy;
    }

    private static class MoonJpaQueryMethodFactory implements JpaQueryMethodFactory {

        private final QueryExtractor extractor;

        public MoonJpaQueryMethodFactory(QueryExtractor extractor) {
            Assert.notNull(extractor, "QueryExtractor must not be null");
            this.extractor = extractor;
        }

        @Override
        public JpaQueryMethod build(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
            return new MoonJpaQueryMethod(method, metadata, factory, this.extractor);
        }
    }

    private static class MoonJpaQueryMethod extends JpaQueryMethod {

        protected MoonJpaQueryMethod(
            Method method, RepositoryMetadata metadata, ProjectionFactory factory, QueryExtractor extractor
        ) { super(method, metadata, factory, extractor); }
    }

    private enum CreateStrategy {
        OLD(EntityManager.class,
            QueryLookupStrategy.Key.class,
            QueryExtractor.class,
            QueryMethodEvaluationContextProvider.class,
            EscapeCharacter.class) {
            @Override
            Object[] create(
                EntityManager em,
                QueryLookupStrategy.Key key,
                QueryExtractor extractor,
                QueryMethodEvaluationContextProvider provider,
                EscapeCharacter escape
            ) {
                return toObjects(em, new MoonJpaQueryMethodFactory(extractor), key, provider, escape);
            }
        },
        NEW(EntityManager.class,
            JpaQueryMethodFactory.class,
            QueryLookupStrategy.Key.class,
            QueryMethodEvaluationContextProvider.class,
            EscapeCharacter.class) {
            @Override
            Object[] create(
                EntityManager em,
                QueryLookupStrategy.Key key,
                QueryExtractor extractor,
                QueryMethodEvaluationContextProvider provider,
                EscapeCharacter escape
            ) {
                return toObjects(em, key, extractor, provider, escape);
            }
        };

        final Class[] classes;

        CreateStrategy(Class... classes) {
            this.classes = classes;
        }

        abstract Object[] create(
            EntityManager em,
            QueryLookupStrategy.Key key,
            QueryExtractor extractor,
            QueryMethodEvaluationContextProvider provider,
            EscapeCharacter escape
        );

        static Object[] toObjects(Object... objects) { return objects; }
    }

    private static class QueryLookupStrategyCreator {

        final static Method creatorMethod;
        final static CreateStrategy STRATEGY;

        static {
            Method resultMethod = null;
            CreateStrategy resultStrategy = null;
            CreateStrategy[] strategies = CreateStrategy.values();
            Method[] methods = JpaQueryLookupStrategy.class.getDeclaredMethods();
            methods:
            for (Method method : methods) {
                if (method.getName().equals("create")) {
                    Class[] types = method.getParameterTypes();
                    for (CreateStrategy strategy : strategies) {
                        Class[] classes = strategy.classes;
                        if (classesEquals(classes, types)) {
                            resultStrategy = strategy;
                            resultMethod = method;
                        }
                    }
                }
            }
            STRATEGY = Objects.requireNonNull(resultStrategy);
            creatorMethod = Objects.requireNonNull(resultMethod);
        }

        static boolean classesEquals(Class[] classes1, Class[] classes2) {
            if (classes1.length == classes2.length) {
                for (int i = 0; i < classes1.length; i++) {
                    if (classes1[i] != classes2[i]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        static QueryLookupStrategy create(
            EntityManager em,
            QueryLookupStrategy.Key key,
            QueryExtractor extractor,
            QueryMethodEvaluationContextProvider provider,
            EscapeCharacter escape
        ) {
            try {
                QueryLookupStrategy strategy = (QueryLookupStrategy) creatorMethod.invoke(null,
                    STRATEGY.create(em, key, extractor, provider, escape));
                return strategy;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
