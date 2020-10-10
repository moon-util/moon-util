package com.moon.spring.data.jpa.factory;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

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
     *
     * @see SimpleJpaRepository
     * @see AbstractRepositoryImpl
     * @see DataStringRepositoryImpl
     */
    JpaRepositoryImplementation newRepository(JpaEntityInformation information, EntityManager em);
}
