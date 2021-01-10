package com.moon.accessor;

import com.moon.accessor.dml.WhereClause;

/**
 * @author benshaoye
 */
public interface Condition {

    /**
     * 两个条件之间的关系，注意与{@link WhereClause#and(Condition)}的区别
     * <p>
     * 这里的{@code and}最终会加括号，如：
     *
     * <pre>
     *     // 在条件中关联，直观的看就是会加括号
     *     // ... WHERE (emp.age < 20 AND emp.name = 'lilei') AND ...
     *     EMP.AGE.lt(20).and(EMP.NAME.is(“lilei”));
     *
     *     // 直观的看就这里不会加括号
     *     // ... WHERE emp.age < 20 AND emp.name = 'hanmeimei' AND ...
     *     xxx.where(EMP.AGE.lt(20)).and(EMP.NAME.is(“lilei”));
     * </pre>
     *
     * @param condition 另一个条件
     *
     * @return 合成条件
     *
     * @see WhereClause#and(Condition)
     * @see WhereClause#or(Condition)
     */
    Condition and(Condition condition);

    /**
     * 两个条件之间的关系，注意与{@link WhereClause#or(Condition)}的区别
     * <p>
     * 这里的{@code and}最终会加括号，如：
     *
     * <pre>
     *     // 在条件中关联，直观的看就是会加括号
     *     // ... WHERE (emp.age < 20 OR emp.name = 'lilei') AND ...
     *     EMP.AGE.lt(20).or(EMP.NAME.is(“lilei”));
     *
     *     // 直观的看就这里不会加括号
     *     // ... WHERE emp.age < 20 OR emp.name = 'hanmeimei' AND ...
     *     xxx.where(EMP.AGE.lt(20)).or(EMP.NAME.is(“lilei”));
     * </pre>
     *
     * @param condition 另一个条件
     *
     * @return 合成条件
     *
     * @see WhereClause#and(Condition)
     * @see WhereClause#or(Condition)
     */
    Condition or(Condition condition);

    /**
     * 该条件是否生效，当{@code effective}值为{@code false}时，此条件最终
     * <p>
     * 不会成为{@code  SQL}查询语句的一部分，如：
     *
     * <pre>
     *     EMP.NAME.is(“john”).on(false)
     * </pre>
     *
     * @param effective 该条件是否生效
     *
     * @return 条件
     */
    Condition on(boolean effective);
}
