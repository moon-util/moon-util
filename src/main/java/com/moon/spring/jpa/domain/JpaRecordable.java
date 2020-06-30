package com.moon.spring.jpa.domain;

import com.moon.more.data.Recordable;
import org.springframework.data.domain.Persistable;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author moonsky
 */
public interface JpaRecordable<ID> extends Persistable<ID>, Recordable<ID>, Cloneable, Serializable {

    /**
     * 设置 ID
     *
     * @param value
     */
    @Override
    @Transient
    void setId(ID value);

    /**
     * Returns the id of the entity.
     *
     * @return the id. Can be {@literal null}.
     */
    @Override
    @Transient
    ID getId();

    /**
     * Returns if the {@code Persistable} is new or was persisted already.
     *
     * @return if {@literal true} the object is new.
     */
    @Override
    @Transient
    boolean isNew();
}
