package com.moon.spring.jpa.repository;

import com.moon.spring.jpa.domain.JpaRecordable;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * @author benshaoye
 */
public class RecordRepositoryFactoryBean<T extends BaseRepository<E>, E extends JpaRecordable<String>>
    extends JpaRepositoryFactoryBean<T, E, String> {

    public RecordRepositoryFactoryBean(Class<? extends T> ri) { super(ri); }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new RecordRepositoryFactory(em);
    }
}
