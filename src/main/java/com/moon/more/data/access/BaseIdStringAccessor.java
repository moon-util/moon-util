package com.moon.more.data.access;

import com.moon.more.model.id.StringIdSupplier;

/**
 * @author benshaoye
 */
public interface BaseIdStringAccessor<T extends StringIdSupplier> extends BaseAccessor<String, T> {}
