package com.moon.accessor.annotation.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表名策略
 * <p>
 * 沿着当前实体类的继承结构向上查找第一个注解的{@link TableModelPolicy}为有效处理策略,
 * <p>
 * 如果一个都没有将按所有属性的默认值处理，通常项目的数据表策略和实体命名策略一致，
 * <p>
 * 一般情况在顶级类声明一个公共父类并添加此注解即可
 * <p>
 * <strong>如:</strong>
 * <pre>
 * &#064;TableModelPolicy(tables = "Tables")
 * public abstract class BaseModel {
 *
 *     private String id;
 * }
 *
 * &#064;TableModelPolicy(tables = "Database") // 实际生效的注解
 * public class UserModel extends BaseModel {
 *
 *     private String username;
 * }
 * </pre>
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TableModelPolicy {

    /**
     * 表访问“组”，可通过静态字段直接访问表名，如: Tables.t_user_info
     *
     * @return 组名，通常一个数据库维护一个组即可
     */
    String tables() default "Tables";

    /**
     * 表名模式: 可以自定义前缀、后缀，中间用“{}”作为表名占位符，如为空就自动默认为: “{}”
     * <p>
     * 实际执行顺序: 实体名去掉前后缀、按表名模式处理、替换{@code pattern}，得到实际表名
     *
     * <strong>如：</strong>
     * <pre>
     * &#064;TableModelPolicy(
     *     pattern = "T_{}",
     *     trimEntityPrefix = "Ch",
     *     trimEntitySuffix = "Entity",
     *     caseMode = CaseMode.UNDERSCORE_LOWERCASE
     * )
     * public class ChUserInfoEntity {
     *     private String id;
     *     private String username;
     *     // ... other codes
     * }
     *
     * 此示例中的处理就是:
     * 1. 去掉实体名前后缀后得到: UserInfo
     * 2. 按模式下划线分割的小写字母处理 UserInfo 得到: user_info
     * 3. 替换表名占位符得到表名: T_user_info (注: 设置的 caseMode 大小写不能影响到这里，这里为了演示故意写成了大小写不统一，实际中请注意)
     * </pre>
     *
     * @return 表名格式
     */
    String pattern() default "{}";

    /**
     * 修剪删除掉实体名前缀，可以声明多个，但只处理第一个匹配的
     * <p>
     * 如:
     * <pre>
     * &#064;TableModelPolicy(trimEntityPrefix = {"Ch", "Cn"})
     * public class ChCnUserInfo {
     *     private String id;
     *     private String username;
     *     // ... other codes
     * }
     *
     * 虽然同时出现了{@code ChCn}但最后只裁掉第一个匹配的{@code Ch}后得到: CnUserInfo
     * </pre>
     *
     * @return 前缀
     */
    String[] trimEntityPrefix() default {};

    /**
     * 修剪删除掉实体名后缀，如: Entity、Model 等
     * <p>
     * 可以声明多个，但只处理第一个匹配的(示例参考{@link #trimEntityPrefix()})
     *
     * @return 后缀
     */
    String[] trimEntitySuffix() default {};

    /**
     * 表名模式，默认小写字母用下划线分割
     *
     * @return 命名策略
     */
    CasePolicy casePolicy() default CasePolicy.UNDERSCORE_LOWERCASE;
}
