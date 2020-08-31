package com.moon.data.jpa.repository;

import com.moon.data.jpa.BaseRepository;
import com.moon.data.jpa.JpaRecord;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
public class DataRepositoryFactoryBean<T extends BaseRepository<E>, E extends JpaRecord<String>>
    extends JpaRepositoryFactoryBean<T, E, String> {

    public DataRepositoryFactoryBean(Class<? extends T> ri) { super(ri); }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new DataRepositoryFactory(em);
    }
}
