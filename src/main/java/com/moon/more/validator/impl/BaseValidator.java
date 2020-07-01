package com.moon.more.validator.impl;

import com.moon.more.validator.annotation.AnyInLongs;
import com.moon.more.validator.annotation.InLongs;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.trimAllWhitespace;

/**
 * 集合注解验证的数值容器
 *
 * @author benshaoye
 */
public abstract class BaseValidator {

    /**
     * 验证十进制整数
     */
    protected final static Predicate<String> NUMERIC = str -> str.matches("\\-?[\\d_]+");
    /**
     * 验证十进制小数
     */
    protected final static Predicate<String> DECIMAL = str -> str.matches("^\\-?[\\d]+\\.([\\d]+)?$") || str.matches(
        "^\\-?[\\d]+(\\.[\\d]+)?$") || str.matches("^\\-?[\\d]*\\.[\\d]+$");

    /**
     * 默认容器类型
     *
     * @return set
     */
    protected final static Set hashSet() { return new HashSet(); }

    /**
     * 当字段类型是包装类的时候是否允许 null 值
     * 基本数据类型不可能为 null
     */
    protected boolean allowNull;
    /**
     * 数值列表容器
     */
    protected final Set set;

    protected BaseValidator(Set set) {this.set = set;}

    /**
     * 支持的注解类型
     *
     * @return
     */
    protected abstract Class<? extends Annotation> getSupportedAnnotation();

    /**
     * 预处理后检测，用于验证数值是否符合格式
     *
     * @param value
     *
     * @return
     */
    protected abstract boolean afterPreTransformTest(String value);

    /**
     * 预处理，去掉所有空白字符
     *
     * @param value
     *
     * @return
     */
    protected String preTransformItem(String value) { return trimAllWhitespace(value); }

    /**
     * 支持的注解类型类全名
     *
     * @return
     */
    protected String getSupportedAnnotationName() { return getSupportedAnnotation().getSimpleName(); }

    /**
     * 分隔符
     *
     * @return
     */
    protected String getDelimiter() { return ","; }

    /**
     * 格式化错误消息，这个错误消息是用于初始化注解验证的时候，不是实际验证中的{@code message}
     *
     * @param val    当前字段值
     * @param values 期望值所在的列表范围，由注解类的{@code values}字段指定
     *               如：{@link AnyInLongs#values()}
     *
     * @return 完整错误消息
     */
    protected String getMessage(Object val, Object values) {
        String template = "Invalid value [%s]. Required in declare @%s#values() of [%s]";
        return format(template, val, getSupportedAnnotationName(), values);
    }

    /**
     * 初始化数值列表
     *
     * @param nullable    是否允许字段为 null
     * @param value       期望的值列表，如：{@link InLongs#values()}
     * @param transformer 字符串至数值的转换器
     */
    protected void initialArgs(boolean nullable, String value, Function<String, ?> transformer) {
        boolean allow = this.allowNull = nullable;
        String[] values = value.split(getDelimiter());
        Set set = this.set;
        for (int i = 0; i < values.length; i++) {
            String val = preTransformItem(values[i]);
            if (afterPreTransformTest(val)) {
                set.add(transformer.apply(val));
            } else if (("".equals(val) || "null".equals(val)) && allow) {
                set.add(null);
            } else {
                throw new IllegalArgumentException(getMessage(val, value));
            }
        }
    }

    /**
     * 实际执行验证字段值的方法
     *
     * @param value 字段值
     *
     * @return 验证通过后返回 true，否则返回 false；通常是当 value 在列表{@link #set}中时返回 true
     */
    protected boolean doValidateValue(Object value) { return originalValid(value); }

    /**
     * 基础验证字段值的方法
     *
     * @param value 字段值
     *
     * @return 验证通过后返回 true，否则返回 false；通常是当 value 在列表{@link #set}中时返回 true
     */
    protected final boolean originalValid(Object value) { return value == null ? allowNull : set.contains(value); }
}
