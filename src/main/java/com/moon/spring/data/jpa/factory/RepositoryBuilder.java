package com.moon.spring.data.jpa.factory;

import com.moon.spring.data.jpa.JpaRecord;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @param <T>  实体类型
 * @param <ID> 主键类型
 *
 * @author moonsky
 */
public interface RepositoryBuilder<T extends JpaRecord<ID>, ID extends Serializable> {

    /**
     * 创建{@code Repository}
     *
     * @param repositoryInformation 接口信息
     * @param information           实体信息
     * @param em                    实体管理器
     * @param metadata              元数据：spring 上下文等
     *
     * @return Repository
     *
     * @see SimpleJpaRepository
     * @see AbstractRepositoryImpl
     * @see DataStringRepositoryImpl
     */
    JpaRepositoryImplementation<T, ID> newRepository(
        RepositoryInformation repositoryInformation,
        JpaEntityInformation<T, ID> information,
        EntityManager em,
        JpaRecordRepositoryMetadata metadata
    );
}
