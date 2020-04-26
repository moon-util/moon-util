package com.moon.spring.jpa.domain;

import com.moon.more.model.id.IdOperator;
import com.moon.more.model.id.IdSupplier;
import com.moon.more.model.BaseSupporter;
import org.springframework.data.domain.Persistable;

/**
 * @author benshaoye
 */
public interface IBaseEntity<ID> extends Persistable<ID>, IdOperator<ID>, IdSupplier<ID>, BaseSupporter, Cloneable {}
