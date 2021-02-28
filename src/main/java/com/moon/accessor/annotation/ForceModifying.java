package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 强制修改
 * <p>
 * 当 update/delete 方法无法直接解析出 WHERE 子句时代表全表更新/删除，
 * 这样可能会比较危险，默认就不会执行任何语句
 * 但如果明确是修改/删除全表数据的情况下，可在方法上添加这个注解此时才会
 * 执行相应操作，如:
 *
 * <pre>
 * public interface UserAccessor {
 *
 *     // 这个方法不会执行任何语句
 *     void deleteAll();
 *
 *     &#064;ForceModifying // 此时会执行语句: DELETE t_user
 *     void deleteAll2();
 * }
 * </pre>
 *
 * @author benshaoye
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForceModifying {}
