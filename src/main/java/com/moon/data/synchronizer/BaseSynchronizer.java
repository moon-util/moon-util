package com.moon.data.synchronizer;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface BaseSynchronizer<T extends Record<ID>, ID> extends BaseAccessor<T, ID> {}
