package com.moon.data.jpa.repository.factory;

import com.moon.data.jpa.JpaRecord;
import com.moon.data.jpa.repository.DataStringRepository;
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
final class DataStringRepositoryImpl<T extends JpaRecord<String>> extends AbstractRepositoryImpl<T, String>
    implements DataStringRepository<T> {

    public DataStringRepositoryImpl(JpaEntityInformation<T, ?> ei, EntityManager em) { super(ei, em); }

    public DataStringRepositoryImpl(Class<T> domainClass, EntityManager em) { super(domainClass, em); }
}
