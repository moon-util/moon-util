package com.moon.data.synchronizer;

import com.moon.data.Record;
import com.moon.data.accessor.DataAccessor;

/**
 * 文档参考{@link com.moon.data.accessor.BaseAccessor}和{@link DataAccessor}
 *
 * @author moonsky
 * @see com.moon.data.accessor.BaseAccessor
 * @see DataAccessor
 */
public interface DataSynchronizer<T extends Record<ID>, ID> extends BaseSynchronizer<T, ID>, DataAccessor<T, ID> {}
