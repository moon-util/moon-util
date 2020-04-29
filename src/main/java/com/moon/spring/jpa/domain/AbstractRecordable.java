package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author benshaoye
 */
@MappedSuperclass
public abstract class AbstractRecordable<ID extends Serializable>
    implements JpaRecordable<ID>, Cloneable, Serializable {

    @Id
    @Column(length = 38)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "com.moon.spring.jpa.identity.Identifier")
    private ID id;

    public AbstractRecordable() { }

    public AbstractRecordable(ID id) { this.id = id; }

    public AbstractRecordable(AbstractRecordable<ID> recordable) { this(recordable.getId()); }

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
     * @see AbstractAuditable#createdBy
     * @see AbstractAuditable#getCreatedBy()
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isNew() { return getId() == null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AbstractRecordable<?> that = (AbstractRecordable<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractRecordable{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
