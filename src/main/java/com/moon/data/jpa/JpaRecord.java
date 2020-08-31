package com.moon.data.jpa;

import com.moon.data.Record;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * @author moonsky
 */
public interface JpaRecord<ID> extends Persistable<ID>, Record<ID>, Cloneable, Serializable {

    /**
     * 设置 ID
     *
     * @param value
     */
    @Override
    void setId(ID value);

    /**
     * Returns the id of the entity.
     *
     * @return the id. Can be {@literal null}.
     */
    @Override
    ID getId();

    /**
     * Returns if the {@code Persistable} is new or was persisted already.
     *
     * @return if {@literal true} the object is new.
     */
    @Override
    boolean isNew();
}
