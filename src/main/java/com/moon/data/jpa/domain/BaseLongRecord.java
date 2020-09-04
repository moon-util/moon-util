package com.moon.data.jpa.domain;

import com.moon.data.Record;

import javax.persistence.MappedSuperclass;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseLongRecord extends AbstractJpaRecord<Long> {

    public BaseLongRecord() { }

    public BaseLongRecord(Long id) { super(id); }

    public BaseLongRecord(Record<Long> recordable) { super(recordable); }
}
