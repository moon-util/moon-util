package com.moon.spring.jpa.identity;

import com.moon.spring.jpa.domain.DataAuditable;
import lombok.Data;

import javax.persistence.Entity;

/**
 * @author benshaoye
 */
@Data
@Entity(name = "tb_pushed_hook_detail")
public class PushedHookDetail extends DataAuditable {

    private String name;

    private String age;

    public PushedHookDetail() {
    }
}
