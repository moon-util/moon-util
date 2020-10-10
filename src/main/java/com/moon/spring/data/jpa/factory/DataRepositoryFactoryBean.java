package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.BaseStringRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
public class DataRepositoryFactoryBean<T extends BaseStringRepository<E>, E extends JpaRecord<String>>
    extends JpaRepositoryFactoryBean<T, E, String> {

    static {
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(Long.class, DataLongRepositoryImpl::new);
        JpaIdentifierUtil.registerIdentifierTypedRepositoryBuilder(String.class, DataStringRepositoryImpl::new);
    }

    public DataRepositoryFactoryBean(Class<? extends T> ri) { super(ri); }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new DataRepositoryFactory(em);
    }
}
