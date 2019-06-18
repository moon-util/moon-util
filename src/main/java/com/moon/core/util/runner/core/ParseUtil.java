package com.moon.core.util.runner.core;

import com.moon.core.lang.CharUtil;
import com.moon.core.lang.SupportUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.runner.Runner;
import com.moon.core.util.runner.RunnerSettings;

import java.lang.reflect.Method;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.runner.core.Constants.DOUBLE;
import static com.moon.core.util.runner.core.Constants.SINGLE;

/**
 * @author benshaoye
 */
public class ParseUtil {

    /**
     * 默认字符串表达式分隔符：DELIMITERS = {"{{", "}}"}
     */
    protected final static String[] DELIMITERS = {"{{", "}}"};

    /**
     * @throws AssertionError 不可实例化
     */
    protected ParseUtil() { noInstanceError(); }

    /*
     * -----------------------------------------------
     * assertions
     * -----------------------------------------------
     */

    final static boolean isNum(int value) { return value > 47 && value < 58; }

    final static boolean isVar(int value) {
        return CharUtil.isLetter(value) || value == '$' || value == '_' || CharUtil.isChinese(value);
    }

    final static boolean isStr(int value) { return value == SINGLE || value == DOUBLE; }

    static boolean isAllConst(AsRunner one, AsRunner... others) {
        if (others == null) { return one.isConst(); }
        if (one.isConst()) {
            for (AsRunner other : others) {
                if (!other.isConst()) { return false; }
            }
        }
        return true;
    }

    /**
     * 运行之前 indexer 指向起始索引
     * 运行完成之后 indexer 指向下一个非空白字符索引或字符串长度
     * 返回当前非空白字符或 -1
     *
     * @param chars
     * @param indexer
     * @param len
     *
     * @return
     */
    final static int nextVal(char[] chars, IntAccessor indexer, final int len) {
        return SupportUtil.skipWhitespaces(chars, indexer, len);
    }

    /*
     * -------------------------------------------------------
     * assertions
     * -------------------------------------------------------
     */

    final static <T> T throwErr(char[] chars, IntAccessor indexer) { return SupportUtil.throwErr(chars, indexer); }

    static AsRunner doThrow(String msg) { throw new IllegalArgumentException(msg); }

    static AsRunner doThrow(Method m) { return doThrow(m.getDeclaringClass(), m.getName()); }

    static AsRunner doThrow(Class source, String name) {
        return doThrow("Can not find method of: " + source + "#" + name + "(...)");
    }

    final static void assertTrue(boolean value, char[] chars, IntAccessor indexer) {
        if (value) {
            return;
        }
        throwErr(chars, indexer);
    }

    final static void assertFalse(boolean value, char[] chars, IntAccessor indexer) {
        if (value) {
            throwErr(chars, indexer);
        }
    }

    /*
     * -----------------------------------------------
     * api
     * -----------------------------------------------
     */

    public final static Runner parse(String expression) { return ParseCore.parse(expression); }

    public final static Runner parse(String expression, String[] delimiters) {
        return ParseDelimiters.parse(expression, delimiters);
    }

    public final static Runner parse(String expression, RunnerSettings settings) {
        return ParseCore.parse(expression, settings);
    }

    public final static Runner parse(String expression, String[] delimiters, RunnerSettings settings) {
        return ParseDelimiters.parse(expression, delimiters, settings);
    }
}
