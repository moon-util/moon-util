package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.moon.core.lang.StringUtil;

import javax.persistence.MappedSuperclass;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseRecordable extends AbstractRecordable<String> {

    public BaseRecordable() { }

    public BaseRecordable(String id) { super(id); }

    public BaseRecordable(AbstractRecordable<String> recordable) { super(recordable); }

    @Override
    @JSONField(serialize = false)
    public boolean isNew() { return StringUtil.isEmpty(getId()); }
}
