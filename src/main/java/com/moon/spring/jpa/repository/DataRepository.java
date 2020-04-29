package com.moon.spring.jpa.repository;

import com.moon.more.data.Recordable;
import com.moon.spring.data.DataAccessor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author benshaoye
 */
@NoRepositoryBean
public interface DataRepository<T extends Recordable<String>> extends BaseRepository<T>, DataAccessor<String, T> {}
