package com.moon.spring.jpa.repository;

import com.moon.more.data.Recordable;
import com.moon.spring.data.BaseAccessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author benshaoye
 */
@NoRepositoryBean
public interface BaseRepository<T extends Recordable<String>>
    extends JpaRepository<T, String>, BaseAccessor<String, T> {}
