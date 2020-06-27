package com.moon.core.enums;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.moon.core.enums.Const.CHAR_MINUS;
import static com.moon.core.lang.StringUtil.deleteChars;
import static java.lang.Character.isWhitespace;

/**
 * 正则表达式集合，用来检查字符串是否匹配规则
 *
 * @author benshaoye
 * @see Testers 用来检查对象是否符合要求
 */
public enum Patterns implements Predicate<CharSequence> {
    /**
     * 数字
     */
    DIGIT("\\d+"),
    /**
     * 小写字母
     */
    LOWER("[a-z]+"),
    /**
     * 大写字母
     */
    UPPER("[A-Z]+"),
    /**
     * 字母
     */
    LETTER("[a-zA-Z]+"),
    /**
     * 居民身份证号
     */
    RESIDENT_ID("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)"),
    /**
     * 汉字
     */
    CHINESE_WORD("[\u4E00-\u9FFF]"),
    /**
     * 汉字
     */
    CHINESE_WORDS("[\u4E00-\u9FFF]+"),
    /**
     * 中国大陆手机号
     */
    CHINESE_MOBILE("(?:0|86|\\+86)?1[3456789]\\d{9}") {
        @Override
        public boolean test(CharSequence str) {
            return super.test(deleteChars(str, ch -> ch == CHAR_MINUS || isWhitespace(ch)));
        }
    },
    /**
     * 中国邮政编码
     */
    CHINESE_ZIP_CODE("[1-9]\\d{5}(?!\\d)"),
    /**
     * 电子邮箱
     */
    EMAIL(
        "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$"),
    /**
     * IP v4
     */
    IPV4("^(([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])$"),
    /**
     * IP v6
     */
    IPV6(
        "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$"),
    /**
     * UUID（标准 UUID 格式）
     */
    UUID("^[\\da-z]{8}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{12}$"),
    /**
     * 简化 UUID（不带横线）
     */
    SIMPLIFY_UUID("^[\\da-z]{32}$"),
    /**
     * hibernate 框架采用的 UUID 格式
     */
    HIBERNATE_UUID("^[\\da-z]{8}-[\\da-z]{8}-[\\da-z]{4}-[\\da-z]{8}-[\\da-z]{4}$"),
    /**
     * GUID
     */
    GUID("^[\\da-z]{8}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{16}$"),
    /**
     * 中国车牌号（兼容新能源）；字母无 I、O，容易和 0、1 混淆
     * 京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领,
     */
    PLATE_NUMBER(
        "^(([京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领][A-Z]((\\d{5}[A-HJK])|([A-HJK]([A-HJ-NP-Z\\d])\\d{4})))|([京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领]\\d{3}\\d{1,3}[领])|([京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领][A-Z][A-HJ-NP-Z\\d]{4}[A-HJ-NP-Z\\d挂学警港澳使领]))$"),
    /**
     * RGB 6位色号
     */
    RGB_COLOR6("^#[0-9a-fA-F]{6}$"),
    /**
     * RGB 3位色号
     */
    RGB_COLOR3("^#[0-9a-f]{3}$", Pattern.CASE_INSENSITIVE),
    /**
     * 日期校验
     */
    DATE("^(\\d{2,4})([\\-/年]?)(\\d{1,2})([\\-/.月]?)(\\d{1,2})日?$"),
    /**
     * 时间校验
     */
    TIME("^\\d{1,2}:\\d{1,2}(:\\d{1,2})?$"),
    ;

    private final static class Cached {

        final static Table<String, Integer, Pattern> CACHE = TableImpl.newWeakHashTable();
    }

    private final Pattern pattern;

    Patterns(String value) { this.pattern = of(value); }

    Patterns(String value, int flags) { this.pattern = of(value, flags); }

    /**
     * 获取当前{@code Pattern}
     *
     * @return Pattern
     */
    public Pattern getPattern() { return pattern; }

    /**
     * 从缓存中查找或重新编译并缓存模式匹配器
     *
     * @param regex 正则表达式字符串
     *
     * @return Pattern
     */
    public static Pattern find(String regex) { return find(regex, 0); }

    /**
     * 从缓存中查找或重新编译并缓存模式匹配器
     *
     * @param regex 正则表达式字符串
     * @param flags 标记
     *
     * @return Pattern
     */
    public static Pattern find(String regex, int flags) {
        Pattern pattern = Cached.CACHE.get(regex, flags);
        if (pattern == null) {
            pattern = of(regex, flags);
            synchronized (Patterns.class) {
                Cached.CACHE.put(regex, flags, pattern);
            }
        }
        return pattern;
    }

    /**
     * 检查字符串是否匹配当前正则表达式规则
     *
     * @param str 待测字符串
     *
     * @return 检查通过返回 true，否则返回 false
     */
    @Override
    public boolean test(CharSequence str) { return getPattern().matcher(str).matches(); }

    /**
     * 编译正则表达式
     *
     * @param regex 表达式模式字符串
     *
     * @return 编译后的匹配器
     */
    public static Pattern of(String regex) { return Pattern.compile(regex); }

    /**
     * 编译正则表达式
     *
     * @param regex 表达式模式字符串
     * @param flags 标记
     *
     * @return 编译后的匹配器
     */
    public static Pattern of(String regex, int flags) { return Pattern.compile(regex, flags); }
}
