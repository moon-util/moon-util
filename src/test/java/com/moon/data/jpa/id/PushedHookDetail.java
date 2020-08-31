package com.moon.data.jpa.id;

import com.moon.data.jpa.domain.DataAuditRecordEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * @author moonsky
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(name = "tb_pushed_hook_detail")
public class PushedHookDetail extends DataAuditRecordEntity {

    private String name;

    private String age;

    public PushedHookDetail() {
    }
}
