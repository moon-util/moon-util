package com.moon.accessor;

import com.moon.accessor.dml.WhereClause;

/**
 * WHERE 条件
 *
 * @author benshaoye
 */
public interface Conditional {

    /**
     * 两个条件之间的关系，注意与{@link WhereClause#and(Conditional)}的区别
     * <p>
     * 这里的{@code and}最终会加括号，逻辑意义也会不一样，如：
     *
     * <pre>
     *     // 在条件中关联，直观的看就是会加括号
     *     // ... WHERE (emp.age < 20 AND emp.name = 'lilei') AND ...
     *     EMP.AGE.lt(20).and(EMP.NAME.is("lilei"));
     *
     *     // 直观的看就这里不会加括号
     *     // ... WHERE emp.age < 20 AND emp.name = 'hanmeimei' AND ...
     *     xxx.where(EMP.AGE.lt(20)).and(EMP.NAME.is("lilei"));
     * </pre>
     *
     * @param condition 另一个条件
     *
     * @return 合成条件
     *
     * @see WhereClause#and(Conditional)
     * @see WhereClause#or(Conditional)
     */
    Conditional and(Conditional condition);

    /**
     * 两个条件之间的关系，注意与{@link WhereClause#or(Conditional)}的区别
     * <p>
     * 这里的{@code and}最终会加括号，逻辑意义也会不一样，如：
     *
     * <pre>
     * // 在条件中关联，直观的看就是会加括号
     * // ... WHERE (emp.age < 20 OR emp.name = 'lilei') ...
     * EMP.AGE.lt(20).or(EMP.NAME.is("lilei"));
     *
     * // 直观的看就这里不会加括号
     * // ... WHERE emp.age < 20 OR emp.name = 'hanmeimei' ...
     * xxx.where(EMP.AGE.lt(20)).or(EMP.NAME.is("lilei"));
     * </pre>
     *
     * @param condition 另一个条件
     *
     * @return 合成条件
     *
     * @see WhereClause#and(Conditional)
     * @see WhereClause#or(Conditional)
     */
    Conditional or(Conditional condition);

    /**
     * 该条件是否生效，当{@code available}值为{@code false}时，此条件最终
     * <p>
     * 不会成为{@code  SQL}查询语句的一部分，它影响的始终是当前条件，举例说明如：
     *
     * <pre>
     * // EMP.NAME 条件不会出现在条件中
     * // ... WHERE age = 20 ...
     * xxx.where(EMP.NAME.is("john").on(false)).and(EMP.AGE.is(20));
     *
     * // ... WHERE name = 'john' ...
     * xxx.where(EMP.NAME.is("john")).and(EMP.AGE.is(20).on(false));
     *
     *
     *
     * // 注意这里 on(false) 位置不同所影响最终生成的条件语句
     * // ... WHERE (name = 'john' AND age = 20) AND address = '北京' ...
     * xxx.where(EMP.NAME.is("john").and(EMP.AGE.is(20))).and(EMP.ADDRESS.eq("北京"));
     *
     * // ... WHERE address = '北京' ...
     * xxx.where(EMP.NAME.is("john").and(EMP.AGE.is(20)).on(false)).and(EMP.ADDRESS.eq("北京"));
     *
     * // ... WHERE name = 'john' AND address = '北京' ...
     * xxx.where(EMP.NAME.is("john").and(EMP.AGE.is(20).on(false))).and(EMP.ADDRESS.eq("北京"));
     *
     * // ... WHERE age = 20 AND address = '北京' ...
     * xxx.where(EMP.NAME.is("john").on(false).and(EMP.AGE.is(20))).and(EMP.ADDRESS.eq("北京"));
     * </pre>
     *
     * @param available 该条件是否生效
     *
     * @return 条件
     */
    Conditional on(boolean available);
}
