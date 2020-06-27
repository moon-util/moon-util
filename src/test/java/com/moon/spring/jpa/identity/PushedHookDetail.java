package com.moon.spring.jpa.identity;

import com.moon.spring.jpa.domain.BaseDataAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;

/**
 * @author benshaoye
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity(name = "tb_pushed_hook_detail")
public class PushedHookDetail extends BaseDataAuditable {

    private String name;

    private String age;

    public PushedHookDetail() {
    }
}
