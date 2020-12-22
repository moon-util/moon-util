package com.moon.data.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记主键字段
 * <p>
 * 列名用{@link TableColumn}定义
 * <p>
 * ID 实际上并不是约束，而是根据业务需求的定义
 * <p>
 * 即：ID 不是必须的，也可以由任意多列组成联合主键
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TableId {}
