package com.moon.spring.data.jpa.factory;

import com.moon.data.identifier.IdentifierUtil;
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
        Class identifierClass = information.getIdType();
        JpaEntityInformation ei = getEntityInformation(information.getDomainType());
        return JpaIdentifierUtil.newRepositoryByIdentifierType(identifierClass, ei, em);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) { return DataStringRepositoryImpl.class; }


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
        QueryLookupStrategy.Key key,
        QueryExtractor extractor,
        QueryMethodEvaluationContextProvider provider,
        EscapeCharacter escape
    ) {
        QueryLookupStrategy strategy = SqlQueryLookupStrategyCreator.create(em, key, extractor, provider, escape);
        return new SqlQueryLookupStrategy(strategy, em, extractor);
    }
}
