package com.moon.spring.jpa;

import com.moon.more.data.Recordable;
import org.springframework.data.domain.Persistable;

/**
 * @author benshaoye
 */
public interface JpaRecordable<ID> extends Persistable<ID>, Recordable<ID> {
}
