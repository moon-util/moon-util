package com.moon.data.jpa.factory;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
public interface RepositoryBuilder {

    /**
     * 创建{@code Repository}
     *
     * @param information 实体信息
     * @param em          实体管理器
     *
     * @return Repository
     */
    JpaRepositoryImplementation newRepository(JpaEntityInformation information, EntityManager em);
}
