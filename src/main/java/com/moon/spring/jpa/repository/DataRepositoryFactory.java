package com.moon.spring.jpa.repository;

import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
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
     * @see JpaQueryLookupStrategy#create(EntityManager, QueryLookupStrategy.Key, QueryExtractor,
     * QueryMethodEvaluationContextProvider, EscapeCharacter)
     */
    public static QueryLookupStrategy create(
        EntityManager em,
        @Nullable QueryLookupStrategy.Key key,
        QueryExtractor extractor,
        QueryMethodEvaluationContextProvider provider,
        EscapeCharacter escape
    ) {
        QueryLookupStrategy strategy = JpaQueryLookupStrategy.create(em, key, extractor, provider, escape);
        switch (key != null ? key : QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND) {
            case CREATE:
            case CREATE_IF_NOT_FOUND:
                strategy = new SqlQueryLookupStrategy(strategy, em, extractor);
                break;
        }
        return strategy;
    }
}
