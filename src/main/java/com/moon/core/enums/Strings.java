package com.moon.core.enums;

import com.moon.core.lang.StringUtil;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public interface Strings {
    /**
     * 空字符串
     */
    String EMPTY = new String();
    /**
     * 点号、句号
     */
    String dot = String.valueOf(Chars.dot);
    /**
     * 空格
     */
    String space = String.valueOf(Chars.space);
    /**
     * 下划线
     */
    String underline = String.valueOf(Chars.underline);
    /**
     * 逗号
     */
    String comma = String.valueOf(Chars.comma);
    /**
     * 减号
     */
    String minus = String.valueOf(Chars.minus);
    /**
     * 冒号
     */
    String colon = String.valueOf(Chars.colon);
    /**
     * 竖线
     */
    String verticalLine = String.valueOf(Chars.verticalLine);

    /**
     * 常见英文符号：键盘上能直接敲出来的英文符号
     */
    String symbolCache = StringUtil.distinctChars("~`!@#$%^&*()_+-={}|[]\\:\";'<>?,./");
    /**
     * 常见中文符号：键盘上能直接敲出来的中文符号
     */
    String ChineseSymbols = StringUtil.distinctChars("~·！@#￥%…&*（）—+-={}|【】、：”；’《》？，。、");
    /**
     * 数字
     */
    String numbers = "0123456789";
    /**
     * 小写字母
     */
    String lowers = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 大写字母
     */
    String uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 字母
     */
    String letters = lowers + uppers;
}
