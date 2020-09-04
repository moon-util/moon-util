package com.moon.data.service;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface BaseStringService<T extends Record<String>> extends BaseAccessor<T, String> {}
