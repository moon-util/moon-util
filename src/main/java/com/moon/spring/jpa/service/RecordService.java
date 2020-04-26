package com.moon.spring.jpa.service;

import com.moon.spring.data.RecordAccessor;
import com.moon.spring.jpa.JpaRecordable;

/**
 * @author benshaoye
 */
public interface RecordService<T extends JpaRecordable<String>> extends RecordAccessor<String, T> {}
