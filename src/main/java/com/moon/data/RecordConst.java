package com.moon.data;

import com.moon.data.jpa.repository.DataRepositoryFactoryBean;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author moonsky
 */
public final class RecordConst {

    private RecordConst() { }

    /**
     * @see Where#clause()
     */
    public final static String WHERE_IDX = Available.WHERE_IDX;
    /**
     * @see Where#clause()
     */
    public final static String WHERE_STR = Available.WHERE_STR;

    /**
     * @see EnableJpaRepositories#repositoryFactoryBeanClass()
     */
    public final static class RepositoryFactoryBean extends DataRepositoryFactoryBean {

        public RepositoryFactoryBean(Class ri) { super(ri); }
    }
}
