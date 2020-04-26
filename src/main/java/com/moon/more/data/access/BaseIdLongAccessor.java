package com.moon.more.data.access;

import com.moon.more.model.id.LongIdSupplier;

/**
 * @author benshaoye
 */
public interface BaseIdLongAccessor<T extends LongIdSupplier> extends BaseAccessor<Long, T> {}
