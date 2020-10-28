package com.moon.spring.data.jpa.factory;

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

import javax.persistence.EntityManager;
import java.util.Optional;

/**
 * @author moonsky
 */
public class JpaRecordRepositoryFactory extends JpaRepositoryFactory {

    protected final EntityManager em;
    private final QueryExtractor extractor;
    private final JpaRecordRepositoryMetadata metadata;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    public JpaRecordRepositoryFactory(EntityManager em, JpaRecordRepositoryMetadata metadata) {
        super(em);
        this.em = em;
        this.metadata = metadata;
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
        return JpaIdentifierUtil.newTargetRepository(information, ei, em, metadata);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return JpaIdentifierUtil.getRepositoryImplementationClass(metadata.getIdType(),
            JpaIdentifierUtil.DefaultRepositoryImpl.class);
    }


    @Override
    public <T> T getRepository(Class<T> repositoryInterface, RepositoryComposition.RepositoryFragments fragments) {
        return super.getRepository(repositoryInterface, fragments);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
        QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider provider
    ) {
        return Optional.of(create(em, key, extractor, provider, escapeCharacter, metadata));
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
        QueryLookupStrategy.Key key,
        QueryExtractor extractor,
        QueryMethodEvaluationContextProvider provider,
        EscapeCharacter escape,
        JpaRecordRepositoryMetadata metadata
    ) {
        QueryLookupStrategy strategy = SqlQueryLookupStrategyCreator.create(em, key, extractor, provider, escape);
        return new SqlQueryLookupStrategy(strategy, em, extractor, metadata);
    }
}
