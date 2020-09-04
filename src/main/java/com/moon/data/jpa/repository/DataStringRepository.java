package com.moon.data.jpa.repository;

import com.moon.data.jpa.JpaRecord;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author moonsky
 */
@NoRepositoryBean
public interface DataStringRepository<T extends JpaRecord<String>> extends DataRepository<T, String> {}
