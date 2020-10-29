package com.moon.data.synchronizer;

import com.moon.data.Record;
import com.moon.data.accessor.DataAccessor;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface DataSynchronizer<T extends Record<ID>, ID> extends BaseSynchronizer<T, ID>, DataAccessor<T, ID> {}
