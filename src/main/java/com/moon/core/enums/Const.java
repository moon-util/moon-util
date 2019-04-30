package com.moon.core.enums;

import java.io.File;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class Const {

    private Const() { noInstanceError(); }

    public static final int ZERO = 0;
    public static final int ONE = 1;

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    public static final String TRUE_STR = String.valueOf(TRUE);
    public static final String FALSE_STR = String.valueOf(FALSE);

    public static final int ERROR_NUMBER = -1;
    /**
     * ","——英文逗号
     */
    public static final Character SEPARATOR_CHAR = Chars.comma;
    /**
     * ","——英文逗号
     */
    public static final String SEPARATOR_STRING = Strings.comma;

    //==============================================================
    //File Separator
    //==============================================================

    public static final char SYS_FileSeparator_Char = File.separatorChar;
    public static final String SYS_FileSeparator = File.separator;

    public static final char WIN_FileSeparator_Char = Character.valueOf((char) 92);
    public static final String WIN_FileSeparator = String.valueOf((char) 92);
    /**
     * '/'
     */
    public static final char App_FileSeparatorChar = (char) 47;
    /**
     * "/"
     */
    public static final String App_FileSeparator = String.valueOf(App_FileSeparatorChar);

    public static final int WIN_FILE_INVALID_CHAR = 65279;
    public static final int DEFAULT_LENGTH = 16;
    /**
     * EMPTY string
     */
    public static final String EMPTY = Strings.EMPTY;
    public static final String NULL_STR = "null";
    public static final String UNDEFINED_STR = "undefined";
}
