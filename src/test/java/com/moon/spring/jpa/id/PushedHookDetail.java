package com.moon.spring.jpa.id;

import com.moon.spring.jpa.domain.BaseDataAuditRecord;
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
public class PushedHookDetail extends BaseDataAuditRecord {

    private String name;

    private String age;

    public PushedHookDetail() {
    }
}
