package com.moon.data.service;

import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;
import com.moon.data.accessor.DataAccessor;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 文档参考{@link com.moon.data.accessor.BaseAccessor}和{@link DataAccessor}
 *
 * @author moonsky
 * @see com.moon.data.accessor.BaseAccessor
 * @see DataAccessor
 */
public interface BaseService<T extends Record<ID>, ID> extends BaseAccessor<T, ID> {}
