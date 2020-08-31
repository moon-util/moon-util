package com.moon.data;

import com.moon.data.jpa.repository.DataRepositoryFactoryBean;

/**
 * @author moonsky
 */
public final class RecordConst {

    private RecordConst() { }

    public final static String WHERE_IDX = Available.WHERE_IDX;

    public final static String WHERE_STR = Available.WHERE_STR;

    public final static Class repositoryFactoryBeanClass = DataRepositoryFactoryBean.class;
}
