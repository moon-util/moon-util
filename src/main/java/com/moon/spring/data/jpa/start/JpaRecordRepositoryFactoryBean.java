package com.moon.spring.data.jpa.start;

import com.moon.spring.data.jpa.factory.DataRepositoryFactoryBean;

/**
 * @author moonsky
 */
public final class JpaRecordRepositoryFactoryBean extends DataRepositoryFactoryBean {

    public JpaRecordRepositoryFactoryBean(Class ri) { super(ri); }
}
