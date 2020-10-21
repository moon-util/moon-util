package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
public class DataRepositoryFactoryBean<ID, T extends BaseRepository<E, ID>, E extends JpaRecord<ID>>
    extends JpaRepositoryFactoryBean<T, E, ID> implements ApplicationContextAware {

    static {
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(Long.class, DataLongRepositoryImpl::new);
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(String.class, DataStringRepositoryImpl::new);
    }

    private ApplicationContext applicationContext;

    public DataRepositoryFactoryBean(Class<? extends T> ri) { super(ri); }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new DataRepositoryFactory(em, new RepositoryContextMetadata(applicationContext));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
