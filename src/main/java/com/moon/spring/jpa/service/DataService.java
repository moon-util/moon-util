package com.moon.spring.jpa.service;

import com.moon.spring.data.DataAccessor;
import com.moon.spring.jpa.domain.JpaRecordable;

/**
 * @author benshaoye
 */
public interface DataService<T extends JpaRecordable<String>> extends DataAccessor<String, T> {}
