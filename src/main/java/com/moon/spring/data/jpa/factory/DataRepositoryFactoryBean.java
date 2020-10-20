package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.BaseRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
public class DataRepositoryFactoryBean<ID, T extends BaseRepository<E, ID>, E extends JpaRecord<ID>>
    extends JpaRepositoryFactoryBean<T, E, ID> implements ApplicationContextAware, ImportAware {

    static {
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(Long.class, DataLongRepositoryImpl::new);
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(String.class, DataStringRepositoryImpl::new);
    }

    private ApplicationContext applicationContext;
    private AnnotationMetadata importMetadata;

    public DataRepositoryFactoryBean(Class<? extends T> ri) { super(ri); }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new DataRepositoryFactory(em, new RepositoryContextMetadata(applicationContext, importMetadata));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.importMetadata = importMetadata;
    }
}
