package com.moon.spring.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.moon.core.lang.StringUtil;
import com.moon.data.Record;

import javax.persistence.MappedSuperclass;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseStringRecord extends AbstractJpaRecord<String> {

    public BaseStringRecord() { }

    public BaseStringRecord(String id) { super(id); }

    public BaseStringRecord(Record<String> recordable) { super(recordable); }

    @Override
    @JSONField(serialize = false)
    public boolean isNew() { return StringUtil.isEmpty(getId()); }
}
