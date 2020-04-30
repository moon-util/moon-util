package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.moon.core.lang.StringUtil;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author benshaoye
 */
@MappedSuperclass
public abstract class BaseAuditable extends AbstractAuditable<String, LocalDateTime> {

    public BaseAuditable() { }

    public BaseAuditable(AbstractAuditable<String, LocalDateTime> audit) { super(audit); }

    public BaseAuditable(String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdBy, updatedBy, createdAt, updatedAt);
    }

    public BaseAuditable(
        String id, String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(id, createdBy, updatedBy, createdAt, updatedAt); }

    @Override
    @JSONField(serialize = false)
    public boolean isNew() { return StringUtil.isEmpty(getId()); }
}
