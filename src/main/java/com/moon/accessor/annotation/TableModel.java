package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单独指定表模型
 *
 * @author benshaoye
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableModel {

    /**
     * 指定表名，为实体单独指定表名后会覆盖公共表名策略
     *
     * @return 指定表名
     */
    String name() default "";

    /**
     * 全局表别名，有的类名可能较长，dsl 语法时写起来比较复杂，可通过设置别名，简化书写时引用。
     * <p>
     * 示例：
     * <pre>
     *  // 定义公共表名策略，参考{@link TableModelPolicy}
     * &#064;TableModelPolicy(pattern = "t_{}", trimEntitySuffix = "Entity")
     * public abstract class BaseEntity {
     *     private String id;
     * }
     *
     * // 1. 直接命名，默认带有命名空间: Aliases
     * &#064;TableModel(alias = "USER")
     * public class UserEntity extends BaseEntity {
     *
     *     private String username;
     * }
     *
     *
     * // 2. 同时指定命名空间
     * &#064;TableModel(
     *     // 指定表名，这里实体类指定的表名会覆盖公共表名策略
     *     // 但不影响公共策略里 tables 声明
     *     // 即还是可以通过{@code Tables.tb_employee} 访问
     *     name = "tb_employee",
     *     alias = "MyAliases.EMP"
     * )
     * public class EmployeeEntity extends BaseEntity {
     *
     *     private String employeeName;
     * }
     *
     * public class MainDemo {
     *
     *     public static void main(String[] args) {
     *         // UserEntity 关联表: t_user
     *         assert Aliases.USER == Tables.t_user;
     *         assert T_USER.TABLE == Tables.t_user;
     *         assert Aliases.USER.id == T_USER.ID;
     *         assert Aliases.USER.id == Tables.t_user.id;
     *
     *         // EmployeeEntity 关联表: tb_employee
     *         assert MyAliases.EMP == Tables.tb_employee;
     *         assert TB_EMPLOYEE.TABLE == Tables.tb_employee;
     *         assert MyAliases.EMP.id == TB_EMPLOYEE.ID;
     *         assert MyAliases.EMP.id == Tables.tb_employee.id;
     *     }
     * }
     * </pre>
     *
     * @return 表别名，可仅指定别名，也可带有命名空间，用英文点号（“.”）分割。
     * 命名空间和别名只能由字母、数字、下划线组成，且不能以数字开头。
     * 如果出现重名的别名，后者覆盖前者或通过命名空间区分。
     */
    String alias() default "";
}
