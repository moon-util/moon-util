package com.moon.spring.data.jpa.repository;

import com.moon.spring.data.jpa.JpaRecord;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author moonsky
 */
@NoRepositoryBean
public interface BaseLongRepository<T extends JpaRecord<Long>> extends BaseRepository<T, Long> {}
