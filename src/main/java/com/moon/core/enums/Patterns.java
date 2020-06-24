package com.moon.core.enums;

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
        "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    // ,,,,,,,,,
    ;

    private final Pattern pattern;

    Patterns(String value) { this.pattern = ofPattern(value); }

    public Pattern getPattern() { return pattern; }

    @Override
    public boolean test(CharSequence str) { return pattern.matcher(str).matches(); }

    public static Pattern ofPattern(String regex) { return Pattern.compile(regex); }
}
