package com.moon.core.enums;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.CalendarUtil.*;

/**
 * @author moonsky
 */
public final class Const {

    private Const() { noInstanceError(); }

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int MINUS_ONE = -1;
    public static final int DEFAULT_SIZE = 16;

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;


    /**
     * ","——英文逗号
     */
    public static final char CHAR_COMMA = Chars.COMMA.value;
    public static final char CHAR_MINUS = Chars.MINUS.value;

    /**
     * string
     */
    public static final String EMPTY = Strings.EMPTY.value;
    public static final String STR_TRUE = String.valueOf(TRUE);
    public static final String STR_FALSE = String.valueOf(FALSE);
    public static final String STR_UNDEFINED = "undefined";
    public static final String STR_NULL = "null";
    public static final String STR_EMPTY = EMPTY;
    /**
     * ","——英文逗号
     */
    public static final String STR_COMMA = Strings.COMMA.value;

    public final static String PATTERN_DATE = yyyy_MM_dd;

    public final static String PATTERN_MONTH = yyyy_MM;

    public final static String PATTERN_TIME = HH_mm_ss;

    public final static String PATTERN = yyyy_MM_dd_HH_mm_ss;

    public static final int WIN_FILE_INVALID_CHAR = 65279;
}
