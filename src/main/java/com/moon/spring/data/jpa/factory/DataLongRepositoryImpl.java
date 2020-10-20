package com.moon.spring.data.jpa.factory;

import com.moon.data.accessor.BaseAccessor;
import com.moon.spring.data.jpa.JpaRecord;
import com.moon.spring.data.jpa.repository.BaseRepository;
import com.moon.spring.data.jpa.repository.DataRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
@NoRepositoryBean
@Transactional(readOnly = true)
final class DataLongRepositoryImpl<T extends JpaRecord<Long>> extends AbstractRepositoryImpl<T, Long>
    implements DataRepository<T, Long>, BaseRepository<T, Long>, BaseAccessor<T, Long> {

    public DataLongRepositoryImpl(
        JpaEntityInformation<T, ?> ei, EntityManager em, RepositoryContextMetadata metadata
    ) { super(ei, em, metadata); }

    public DataLongRepositoryImpl(Class<T> domainClass, EntityManager em, RepositoryContextMetadata metadata) {
        super(domainClass, em, metadata);
    }
}
