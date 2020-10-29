package com.moon.data.synchronizer;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface BaseStringSynchronizer<T extends Record<String>> extends BaseAccessor<T, String> {}
