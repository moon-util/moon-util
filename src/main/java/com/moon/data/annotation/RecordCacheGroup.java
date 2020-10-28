package com.moon.data.annotation;

import com.moon.data.RecordConst;
import com.moon.spring.data.jpa.start.EnableJpaRecordCaching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 局部命名空间分组，用于在某个实体上单独覆盖全局分组{@link EnableJpaRecordCaching#group()}
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordCacheGroup {

    String value() default RecordConst.CACHE_GROUP;
}
