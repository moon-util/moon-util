package com.moon.spring.data.jpa.factory;

import com.moon.data.accessor.BaseAccessor;
import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.BaseRepository;
import com.moon.spring.data.jpa.repository.DataRepository;
import com.moon.spring.data.jpa.repository.DataStringRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
public class JpaRecordRepositoryFactoryBean<ID, T extends BaseRepository<E, ID>, E extends JpaRecord<ID>>
    extends JpaRepositoryFactoryBean<T, E, ID> {

    static {
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(Long.class,
            DataLongRepositoryImpl::new,
            DataLongRepositoryImpl.class);
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(String.class,
            DataStringRepositoryImpl::new,
            DataStringRepositoryImpl.class);
    }

    private Class<? extends T> repositoryInterface;
    private JpaQueryMethodFactory queryMethodFactory;
    private ApplicationContext applicationContext;
    private EntityPathResolver entityPathResolver;
    private EscapeCharacter escapeCharacter;
    private EntityManager entityManager;

    public JpaRecordRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    @Autowired
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        super.setEntityPathResolver(resolver);
        this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
    }

    @Override
    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
        this.entityManager = entityManager;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEscapeCharacter(char escapeCharacter) {
        super.setEscapeCharacter(escapeCharacter);
        this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
    }

    @Override
    @Autowired
    public void setQueryMethodFactory(JpaQueryMethodFactory factory) {
        super.setQueryMethodFactory(factory);
        if (factory != null) {
            this.queryMethodFactory = factory;
        }
    }

    public JpaQueryMethodFactory getQueryMethodFactory() { return queryMethodFactory; }

    public Class<? extends T> getRepositoryInterface() { return repositoryInterface; }

    public EntityPathResolver getEntityPathResolver() { return entityPathResolver; }

    public EscapeCharacter getEscapeCharacter() { return escapeCharacter; }

    public EntityManager getEntityManager() { return entityManager; }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        JpaRecordRepositoryFactory factory = new JpaRecordRepositoryFactory(em, getJpaMetadata());
        factory.setEntityPathResolver(getEntityPathResolver());
        factory.setEscapeCharacter(getEscapeCharacter());
        JpaQueryMethodFactory queryMethodFactory = getQueryMethodFactory();
        if (queryMethodFactory != null) {
            factory.setQueryMethodFactory(queryMethodFactory);
        }
        return factory;
    }

    private JpaRecordRepositoryMetadata getJpaMetadata() {
        return new JpaRecordRepositoryMetadata(getContext(),
            getRepositoryInformation(),
            getEntityInformation(),
            getRepositoryInterface());
    }

    private ApplicationContext getContext() {
        try {
            ApplicationContext ctx = applicationContext;
            return ctx == null ? RecordRepositoryNones.Ctx.CTX : ctx;
        } catch (Throwable ignored) {
            return RecordRepositoryNones.Ctx.CTX;
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
    }

    @NoRepositoryBean
    @Transactional(readOnly = true)
    static class DataLongRepositoryImpl<T extends JpaRecord<Long>> extends AbstractRepositoryImpl<T, Long>
        implements DataRepository<T, Long>, BaseRepository<T, Long>, BaseAccessor<T, Long> {

        public DataLongRepositoryImpl(
            RepositoryInformation repositoryInformation,
            JpaEntityInformation<T, ?> ei,
            EntityManager em,
            JpaRecordRepositoryMetadata metadata
        ) { super(repositoryInformation, ei, em, metadata); }
    }

    @NoRepositoryBean
    @Transactional(readOnly = true)
    static class DataStringRepositoryImpl<T extends JpaRecord<String>> extends AbstractRepositoryImpl<T, String>
        implements DataStringRepository<T> {

        public DataStringRepositoryImpl(
            RepositoryInformation repositoryInformation,
            JpaEntityInformation<T, ?> ei,
            EntityManager em,
            JpaRecordRepositoryMetadata metadata
        ) { super(repositoryInformation, ei, em, metadata); }
    }
}
