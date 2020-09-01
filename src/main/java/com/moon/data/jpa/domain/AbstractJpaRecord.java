package com.moon.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.data.jpa.JpaRecord;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author moonsky
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractJpaRecord<ID extends Serializable>
    implements JpaRecord<ID>, Cloneable, Serializable {

    @Id
    @Column(length = 38)
    @GeneratedValue(generator = "AbstractJpaRecordGenerator")
    @GenericGenerator(name = "AbstractJpaRecordGenerator", strategy = "com.moon.data.jpa.id.Identifier")
    private ID id;

    public AbstractJpaRecord() { }

    public AbstractJpaRecord(ID id) { this.id = id; }

    public AbstractJpaRecord(AbstractJpaRecord<ID> recordable) { this(recordable.getId()); }

    @Override
    public ID getId() { return id; }

    @Override
    public void setId(ID id) { this.id = id; }

    /**
     * 是否是新对象
     *
     * @return true or false
     *
     * @see JsonIgnore 参考 JsonIgnore 和 JSONField 的使用规则
     * @see JSONField
     * @see AbstractJpaAuditRecord#createdBy
     * @see AbstractJpaAuditRecord#getCreatedBy()
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isNew() { return getId() == null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AbstractJpaRecord<?> that = (AbstractJpaRecord<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractJpaRecord{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
