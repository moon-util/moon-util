package com.moon.data.jpa.factory;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import javax.persistence.EntityManager;

/**
 * @author moonsky
 */
public interface TypedRepositoryBuilder {

    JpaRepositoryImplementation newRepository(JpaEntityInformation information, EntityManager em);
}
