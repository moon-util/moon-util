package com.moon.data;

import com.moon.core.lang.Moon;
import com.moon.data.jpa.id.RecordIdentifierGenerator;
import com.moon.data.jpa.repository.DataRepositoryFactoryBean;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author moonsky
 */
public final class RecordConst {

    private RecordConst() { }

    /**
     * moon.data.jpa.identifier
     *
     * @see RecordIdentifierGenerator#RecordIdentifierGenerator()
     */
    public final static String jpaIdentifierKey = Moon.Data.Jpa.IDENTIFIER;

    /**
     * 逻辑删除用
     *
     * @see Where#clause()
     * @see Record#WHERE_IDX
     */
    public final static String WHERE_IDX = Available.WHERE_IDX;
    /**
     * 逻辑删除用
     *
     * @see Where#clause()
     * @see Record#WHERE_STR
     */
    public final static String WHERE_STR = Available.WHERE_STR;

    /**
     * @see EnableJpaRepositories#repositoryFactoryBeanClass()
     */
    public final static class RepositoryFactoryBean extends DataRepositoryFactoryBean {

        public RepositoryFactoryBean(Class ri) { super(ri); }
    }
}
