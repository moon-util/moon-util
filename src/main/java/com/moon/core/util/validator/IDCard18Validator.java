package com.moon.core.util.validator;

import com.moon.core.time.DatetimeUtil;
import com.moon.core.util.DateUtil;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Integer.valueOf;

/**
 * @author benshaoye
 */
public final class IDCard18Validator extends BaseValidator<String, IDCard18Validator> {

    private final static int INVALID_ITEM_VALUE = -1;
    /**
     * 出生年份
     */
    private final int birthYear;
    /**
     * 出生月份
     */
    private final int birthMonth;
    /**
     * 出生日子
     */
    private final int birthDay;
    /**
     * 性别
     */
    private final int gender;

    public static boolean isValid(String value) {
        if (value == null || value.length() != 18) {
            return false;
        }
        int result = 0;
        final int end = 17, mod = 11;
        final int[] cODES = {
            '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'
        };
        final int[] codes = {
            7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2
        };
        for (int i = 0, curr; i < end; i++) {
            curr = value.charAt(i);
            if (curr < 48 || curr > 57) {
                return false;
            }
            result += (curr - 48) * codes[i];
        }
        final int index = result % mod;
        return index >= 0 && value.charAt(end) == cODES[index];
    }

    public IDCard18Validator(String value) { this(value, null, SEPARATOR, false); }

    private IDCard18Validator(String value, List<String> messages, String separator, boolean immediate) {
        super(value == null ? "" : value, false, messages, separator, immediate);
        boolean isValid = require(IDCard18Validator::isValid, null).isValid();
        this.birthYear = isValid ? valueOf(value.substring(6, 10)) : INVALID_ITEM_VALUE;
        this.birthMonth = isValid ? valueOf(value.substring(10, 12)) : INVALID_ITEM_VALUE;
        this.birthDay = isValid ? valueOf(value.substring(12, 14)) : INVALID_ITEM_VALUE;
        this.gender = isValid ? (value.charAt(16) - 48) % 2 : INVALID_ITEM_VALUE;
    }

    public static IDCard18Validator of(String value) { return new IDCard18Validator(value); }

    public Date getBirthday() { return DateUtil.toDate(birthYear, birthMonth, birthDay, 0, 0, 0); }

    public IDCard18Validator ifValidated(Consumer<IDCard18Validator> consumer) {
        if (isValid()) { consumer.accept(this); }
        return this;
    }

    public IDCard18Validator ifInvalidated(Consumer<IDCard18Validator> consumer) {
        if (isInvalid()) { consumer.accept(this); }
        return this;
    }

    public boolean isMale() { return gender == 1; }

    public boolean isFemale() { return gender == 0; }

    /**
     * 周岁
     *
     * @return 周岁年龄
     */
    public int getAge() { return DatetimeUtil.toDate(birthYear, birthMonth, birthDay).until(LocalDate.now()).getYears(); }

    /**
     * 虚岁
     *
     * @return 虚岁年龄
     */
    public int getNominalAge() { return getAge() + 1; }
}
