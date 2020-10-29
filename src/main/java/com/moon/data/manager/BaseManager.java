package com.moon.data.manager;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface BaseManager<T extends Record<ID>, ID> extends BaseAccessor<T, ID> {}
