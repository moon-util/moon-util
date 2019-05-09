package com.moon.core.util.asserts;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.moon.core.util.asserts.Use.*;

/**
 * @author benshaoye
 */
public enum Assertion {
    INSTANCE;

    /*
     * -------------------------------------------------------------------------------
     * is true
     * -------------------------------------------------------------------------------
     */

    public final Assertion isTrue(boolean value) { return isTrue(value, "只能是 true."); }

    public final Assertion isTrue(boolean value, String message) {
        if (!value) { err(message); }
        return this;
    }

    public final Assertion isTrue(boolean value, String template, Object... objects) {
        if (!value) { err(template, objects); }
        return this;
    }

    public final Assertion isTrue(Object value) { return isTrue(value, "只能是 true."); }

    public final Assertion isTrue(Object value, String message) {
        if (Boolean.TRUE.equals(value)) { err(message); }
        return this;
    }

    public final Assertion isTrue(Object value, String template, Object... objects) {
        if (Boolean.FALSE.equals(value)) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * not false
     * -------------------------------------------------------------------------------
     */

    public final Assertion isFalse(boolean value) { return isFalse(value, "只能是 false."); }

    public final Assertion isFalse(boolean value, String message) {
        if (value) { err(message); }
        return this;
    }

    public final Assertion isFalse(boolean value, String template, Object... objects) {
        if (value) { err(template, objects); }
        return this;
    }

    public final Assertion isFalse(Object value) { return isFalse(value, "只能是 true."); }

    public final Assertion isFalse(Object value, String message) {
        if (Boolean.FALSE.equals(value)) { err(message); }
        return this;
    }

    public final Assertion isFalse(Object value, String template, Object... objects) {
        if (Boolean.TRUE.equals(value)) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * is null
     * -------------------------------------------------------------------------------
     */

    public final Assertion isNull(Object value) { return isNull(value, "只能是 null 值"); }

    public final Assertion isNull(Object value, String message) {
        if (value != null) { err(message); }
        return this;
    }

    public final Assertion isNull(Object value, String template, Object... objects) {
        if (value != null) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * not null
     * -------------------------------------------------------------------------------
     */

    public final Assertion notNull(Object value) { return notNull(value, "不能是 null 值"); }

    public final Assertion notNull(Object value, String message) {
        if (value == null) { err(message); }
        return this;
    }

    public final Assertion notNull(Object value, String template, Object... objects) {
        if (value == null) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * is empty
     * -------------------------------------------------------------------------------
     */

    public final Assertion isEmpty(CharSequence value) { return isEmpty(value, "必须是空字符串"); }

    public final Assertion isEmpty(CharSequence value, String message) {
        if (!emptyS(value)) { err(message); }
        return this;
    }

    public final Assertion isEmpty(CharSequence value, String template, Object... objects) {
        if (!emptyS(value)) { err(template, objects); }
        return this;
    }

    public final Assertion isEmpty(Collection value) { return isEmpty(value, "必须是空集合"); }

    public final Assertion isEmpty(Collection value, String message) {
        if (!emptyC(value)) { err(message); }
        return this;
    }

    public final Assertion isEmpty(Collection value, String template, Object... objects) {
        if (!emptyC(value)) { err(template, objects); }
        return this;
    }

    public final Assertion isEmpty(Map value) { return isEmpty(value, "必须是空 java.util.Map"); }

    public final Assertion isEmpty(Map value, String message) {
        if (!emptyM(value)) { err(message); }
        return this;
    }

    public final Assertion isEmpty(Map value, String template, Object... objects) {
        if (!emptyM(value)) { err(template, objects); }
        return this;
    }

    private final static String isEmptyMessage = "只能是 null 值、空字符串、空集合、空 Map、空数组、不包含元素的枚举类型";

    public final Assertion isEmpty(Object value) {return isEmpty(value, isEmptyMessage); }

    public final Assertion isEmpty(Object value, String message) {
        if (!emptyO(value)) { err(message); }
        return this;
    }

    public final Assertion isEmpty(Object value, String template, Object... objects) {
        if (!emptyO(value)) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * not empty
     * -------------------------------------------------------------------------------
     */

    public final Assertion notEmpty(CharSequence value) { return notEmpty(value, "不能是空字符串"); }

    public final Assertion notEmpty(CharSequence value, String message) {
        if (emptyS(value)) { err(message); }
        return this;
    }

    public final Assertion notEmpty(CharSequence value, String template, Object... objects) {
        if (emptyS(value)) { err(template, objects); }
        return this;
    }

    public final Assertion notEmpty(Collection value) { return notEmpty(value, "不能是空集合"); }

    public final Assertion notEmpty(Collection value, String message) {
        if (emptyC(value)) { err(message); }
        return this;
    }

    public final Assertion notEmpty(Collection value, String template, Object... objects) {
        if (emptyC(value)) { err(template, objects); }
        return this;
    }

    public final Assertion notEmpty(Map value) { return notEmpty(value, "不能是空 java.util.Map"); }

    public final Assertion notEmpty(Map value, String message) {
        if (emptyM(value)) { err(message); }
        return this;
    }

    public final Assertion notEmpty(Map value, String template, Object... objects) {
        if (emptyM(value)) { err(template, objects); }
        return this;
    }

    private final static String notEmptyMessage = "不能是 null 值、空字符串、空集合、空 Map、空数组、不包含元素的枚举类型";

    public final Assertion notEmpty(Object value) { return notEmpty(value, notEmptyMessage); }

    public final Assertion notEmpty(Object value, String message) {
        if (emptyO(value)) { err(message); }
        return this;
    }

    public final Assertion notEmpty(Object value, String template, Object... objects) {
        if (emptyO(value)) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * is blank
     * -------------------------------------------------------------------------------
     */

    public final Assertion isBlank(CharSequence value) { return isBlank(value, "必须是空白字符串"); }

    public final Assertion isBlank(CharSequence value, String message) {
        if (!blank(value)) { err(message); }
        return this;
    }

    public final Assertion isBlank(CharSequence value, String template, Object... objects) {
        if (!blank(value)) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * not blank
     * -------------------------------------------------------------------------------
     */

    public final Assertion notBlank(CharSequence value) { return notBlank(value, "不能是空白字符串"); }

    public final Assertion notBlank(CharSequence value, String message) {
        if (blank(value)) { err(message); }
        return this;
    }

    public final Assertion notBlank(CharSequence value, String template, Object... objects) {
        if (blank(value)) { err(template, objects); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * compares
     * -------------------------------------------------------------------------------
     */

    public final Assertion gt(int v1, int v2) { return gt(v1, v2, "v1 必须大于 v2"); }

    public final Assertion gt(int v1, int v2, String message) {
        if (v1 <= v2) { err(message); }
        return this;
    }

    public final Assertion lt(int v1, int v2) { return lt(v1, v2, "v1 必须小于 v2"); }

    public final Assertion lt(int v1, int v2, String message) {
        if (v1 >= v2) { err(message); }
        return this;
    }

    public final Assertion eq(int v1, int v2) { return eq(v1, v2, "v1 必须等于 v2"); }

    public final Assertion eq(int v1, int v2, String message) {
        if (v1 == v2) { err(message); }
        return this;
    }

    public final Assertion gt(long v1, long v2) { return gt(v1, v2, "v1 必须大于 v2"); }

    public final Assertion gt(long v1, long v2, String message) {
        if (v1 <= v2) { err(message); }
        return this;
    }

    public final Assertion lt(long v1, long v2) { return lt(v1, v2, "v1 必须小于 v2"); }

    public final Assertion lt(long v1, long v2, String message) {
        if (v1 >= v2) { err(message); }
        return this;
    }

    public final Assertion eq(long v1, long v2) { return eq(v1, v2, "v1 必须等于 v2"); }

    public final Assertion eq(long v1, long v2, String message) {
        if (v1 == v2) { err(message); }
        return this;
    }

    public final Assertion gt(double v1, double v2) { return gt(v1, v2, "v1 必须大于 v2"); }

    public final Assertion gt(double v1, double v2, String message) {
        if (v1 <= v2) { err(message); }
        return this;
    }

    public final Assertion lt(double v1, double v2) { return lt(v1, v2, "v1 必须小于 v2"); }

    public final Assertion lt(double v1, double v2, String message) {
        if (v1 >= v2) { err(message); }
        return this;
    }

    public final Assertion eq(double v1, double v2) { return eq(v1, v2, "v1 必须等于 v2"); }

    public final Assertion eq(double v1, double v2, String message) {
        if (v1 == v2) { err(message); }
        return this;
    }

    public final Assertion isEquals(Object v1, Object v2) { return isEquals(v1, v2, "v1 equals v2"); }

    public final Assertion isEquals(Object v1, Object v2, String message) {
        if (Objects.equals(v1, v2)) { err(message); }
        return this;
    }

    public final Assertion notEquals(Object v1, Object v2) { return notEquals(v1, v2, "v1 equals v2"); }

    public final Assertion notEquals(Object v1, Object v2, String message) {
        if (!Objects.equals(v1, v2)) { err(message); }
        return this;
    }

    /*
     * -------------------------------------------------------------------------------
     * is all null
     * -------------------------------------------------------------------------------
     */

    public final Assertion noNullElement(Iterable value) { return noNullElement(value, "集合所有项不能有 null 值"); }

    public final Assertion noNullElement(Iterable value, String message) {
        if (hasNull(value)) { err(message); }
        return this;
    }

    public final Assertion noNullElement(Iterable value, String template, Object... objects) {
        if (hasNull(value)) { err(template, objects); }
        return this;
    }

    public final Assertion noNullValue(Map value) { return noNullValue(value, "集合所有项不能有 null 值"); }

    public final Assertion noNullValue(Map value, String message) {
        if (hasNullVal(value)) { err(message); }
        return this;
    }

    public final Assertion noNullValue(Map value, String template, Object... objects) {
        if (hasNullVal(value)) { err(template, objects); }
        return this;
    }

    public final Assertion noNullElement(Object[] value) { return noNullElement(value, "集合所有项不能有 null 值"); }

    public final Assertion noNullElement(Object[] value, String message) {
        if (hasNull(value)) { err(message); }
        return this;
    }

    public final Assertion noNullElement(Object[] value, String template, Object... objects) {
        if (hasNull(value)) { err(template, objects); }
        return this;
    }
}
