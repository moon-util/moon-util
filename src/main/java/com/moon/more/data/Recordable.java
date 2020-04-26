package com.moon.more.data;

import com.moon.more.model.BaseSupporter;
import com.moon.more.model.id.IdOperator;
import com.moon.more.model.id.IdSupplier;

/**
 * @author benshaoye
 */
public interface Recordable<ID> extends IdOperator<ID>, IdSupplier<ID>, BaseSupporter, Cloneable {}
