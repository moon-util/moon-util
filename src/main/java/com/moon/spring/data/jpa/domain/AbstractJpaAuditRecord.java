package com.moon.spring.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.data.Record;
import com.moon.spring.data.jpa.JpaRecord;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

/**
 * @param <ID>        可序列化的 id 类型
 * @param <DATE_TYPE> 日期类型：Date，LocalDate，LocalDateTime 等
 *
 * @author moonsky
 * @see #getCreatedBy()
 * @see #createdBy
 * @see JsonIgnore
 * @see JSONField
 */
@MappedSuperclass
public abstract class AbstractJpaAuditRecord<ID extends Serializable, DATE_TYPE> extends AbstractJpaRecord<ID>
    implements JpaRecord<ID>, Cloneable, Serializable {

    /**
     * {@link JsonIgnore}无论注解在字段还是{@code getter}、{@code setter}方法上，该字段都会被完全忽略
     * <p>
     * 如果要实现在 jackson 序列化成 json 时能正常保留被{@link JsonIgnore}注解字段的值，
     * 可以定义另一个命名的{@code getter}, {@code setter}方法来获取，可以这样：
     * <pre>
     *     public class SubAuditable extends AbstractAuditable {
     *
     *         // ... other codes
     *
     *         public String getCreatedUserId() {
     *             return super.getCreatedBy();
     *         }
     *
     *         public void setCreatedUserId(String id) {
     *             super.setCreatedBy(id);
     *         }
     *     }
     * </pre>
     *
     * @see #getCreatedBy()
     * @see JsonIgnore
     * @see JSONField
     */
    @JsonIgnore
    @CreatedBy
    @Column(updatable = false)
    private ID createdBy;

    @JsonIgnore
    @LastModifiedBy
    private ID updatedBy;

    @JsonIgnore
    @CreatedDate
    @Column(updatable = false)
    private DATE_TYPE createdAt;

    @JsonIgnore
    @LastModifiedDate
    private DATE_TYPE updatedAt;

    public AbstractJpaAuditRecord() { }

    public AbstractJpaAuditRecord(Record<ID> record) {
        super(record);
    }

    public AbstractJpaAuditRecord(AbstractJpaAuditRecord<ID, DATE_TYPE> audit) {
        this(audit.getId(), audit.getCreatedBy(), audit.getUpdatedBy(), audit.getCreatedAt(), audit.getUpdatedAt());
    }

    public AbstractJpaAuditRecord(ID createdBy, ID updatedBy, DATE_TYPE createdAt, DATE_TYPE updatedAt) {
        this(null, createdBy, updatedBy, createdAt, updatedAt);
    }

    public AbstractJpaAuditRecord(ID id, ID createdBy, ID updatedBy, DATE_TYPE createdAt, DATE_TYPE updatedAt) {
        super(id);
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 如果{@link JSONField)}注解在{@code getter}上，会在序列化是起效
     * 如果{@link JSONField}注解在{@code setter}上，会在反序列化是起效
     * <p>
     * 默认审计字段不会序列化，如实际中需要序列化，可重写{@code getter}方法，
     * 重写的方法不加{@code @JSONField(serialize = false)}即可
     * <p>
     * <p>
     * 但是{@link JsonIgnore}就不一样了，只要注解了，不论是在字段、getter、setter
     * 方法上，都会被忽略，而且继承重写都没法阻止忽略，所以这里不添加这个
     *
     * @return
     *
     * @see #createdBy
     * @see JsonIgnore
     * @see JSONField
     */
    @JSONField(serialize = false)
    public ID getCreatedBy() { return createdBy; }

    public void setCreatedBy(ID createdBy) { this.createdBy = createdBy; }

    @JSONField(serialize = false)
    public ID getUpdatedBy() { return updatedBy; }

    public void setUpdatedBy(ID updatedBy) { this.updatedBy = updatedBy; }

    @JSONField(serialize = false)
    public DATE_TYPE getCreatedAt() { return createdAt; }

    public void setCreatedAt(DATE_TYPE createdAt) { this.createdAt = createdAt; }

    @JSONField(serialize = false)
    public DATE_TYPE getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(DATE_TYPE updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        AbstractJpaAuditRecord<?, ?> that = (AbstractJpaAuditRecord<?, ?>) o;
        return Objects.equals(createdBy, that.createdBy) && Objects.equals(updatedBy, that.updatedBy) && Objects.equals(
            createdAt,
            that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdBy, updatedBy, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractJpaAuditRecord{");
        sb.append(super.toString());
        sb.append("createdBy=").append(createdBy);
        sb.append(", updatedBy=").append(updatedBy);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}
