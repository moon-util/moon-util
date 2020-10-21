package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.DataStringRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
@NoRepositoryBean
@Transactional(readOnly = true)
final class DataStringRepositoryImpl<T extends JpaRecord<String>> extends AbstractRepositoryImpl<T, String>
    implements DataStringRepository<T> {

    public DataStringRepositoryImpl(
        RepositoryInformation repositoryInformation,
        JpaEntityInformation<T, ?> ei,
        EntityManager em,
        RepositoryContextMetadata metadata
    ) { super(repositoryInformation, ei, em, metadata); }

    public DataStringRepositoryImpl(Class<T> domainClass, EntityManager em, RepositoryContextMetadata metadata) {
        super(domainClass, em, metadata);
    }
}
