package com.moon.data.manager;

import com.moon.data.Record;
import com.moon.data.accessor.DataAccessor;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public interface DataManager<T extends Record<ID>, ID> extends BaseManager<T, ID>, DataAccessor<T, ID> {}
