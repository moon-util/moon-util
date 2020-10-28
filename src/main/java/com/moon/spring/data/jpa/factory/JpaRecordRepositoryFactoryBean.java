package com.moon.spring.data.jpa.factory;

import com.moon.data.accessor.BaseAccessor;
import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.BaseRepository;
import com.moon.spring.data.jpa.repository.DataRepository;
import com.moon.spring.data.jpa.repository.DataStringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
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

    private ApplicationContext applicationContext;

    public JpaRecordRepositoryFactoryBean(Class<? extends T> ri) { super(ri); }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new JpaRecordRepositoryFactory(em, getJpaMetadata());
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private JpaRecordRepositoryMetadata getJpaMetadata() {
        try {
            return new JpaRecordRepositoryMetadata(applicationContext);
        } catch (Throwable t) {
            return new JpaRecordRepositoryMetadata(RecordRepositoryNones.Ctx.CTX);
        }
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
