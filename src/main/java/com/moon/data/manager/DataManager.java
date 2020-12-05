package com.moon.data.manager;

import com.moon.data.Record;
import com.moon.data.accessor.DataAccessor;

/**
 * 文档参考{@link com.moon.data.accessor.BaseAccessor}和{@link DataAccessor}
 *
 * @author moonsky
 * @see com.moon.data.accessor.BaseAccessor
 * @see DataAccessor
 */
@SuppressWarnings("all")
public interface DataManager<T extends Record<ID>, ID> extends BaseManager<T, ID>, DataAccessor<T, ID> {}
