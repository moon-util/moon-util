package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.moon.core.lang.StringUtil;

import javax.persistence.MappedSuperclass;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseRecord extends AbstractRecord<String> {

    public BaseRecord() { }

    public BaseRecord(String id) { super(id); }

    public BaseRecord(AbstractRecord<String> recordable) { super(recordable); }

    @Override
    @JSONField(serialize = false)
    public boolean isNew() { return StringUtil.isEmpty(getId()); }
}
