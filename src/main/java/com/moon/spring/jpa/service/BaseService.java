package com.moon.spring.jpa.service;

import com.moon.spring.data.BaseAccessor;
import com.moon.spring.jpa.domain.JpaRecordable;

/**
 * @author benshaoye
 */
public interface BaseService<T extends JpaRecordable<String>> extends BaseAccessor<String, T> {}
