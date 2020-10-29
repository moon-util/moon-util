package com.moon.data.manager;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface BaseStringManager<T extends Record<String>> extends BaseAccessor<T, String> {}
